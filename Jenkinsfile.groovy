/*
######################################【待配置项】#########################################################
*      1.如果报无法找到Jenkinsfile.yaml，请根据enkinsfile.groovy的实际路径替换"Jenkinsfile.yaml"，默认以项目检出目录为准；
*      2.将文件名修改为"Jenkinsfile.groovy"，jenkins任务构建配置仅执行名为Jenkinsfile.grovy的pipeline
*      3.更多配置说明请访问Confluence( http://confluence.flaginfo.com.cn/pages/viewpage.action?pageId=27011891 )
*      Version：2.1
*############################################################################################################
*/
pipeline {
    agent{
        label 'master'
    }
    environment {
        DEPLOY_VERSION_TYPE = "BIG"
    }
    tools {
        jdk 'JDK-1.8'
        maven 'maven-3.5.4'
        nodejs 'node-v6.11.3'
    }
    parameters{
        string(name: 'JIRA_ISSUE_STATUS_PARAM', defaultValue: 'nullValue',description:"")
    }
    options {
        timeout(time: 10, unit: 'DAYS')
    }
    stages {
        stage ('全局配置') {
            when {
                expression {
                    params.JIRA_ISSUE_STATUS == "进行中" || params.JIRA_ISSUE_STATUS == "测试环境"
                }
            }
            steps {
                script {
                    def yaml_file_exist = fileExists "Jenkinsfile.yaml"
                    if (!yaml_file_exist) {
                        error("Pipeline配置文件丢失，发布失败。")
                        currentBuild.result = "FAILURE"
                    }
                    def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
                    def jira_issue = jiraGetIssue(idOrKey: JIRA_ISSUE_KEY, site: "JIRA")
                    def jira_issue_summary = jira_issue?.data?.fields?.summary?.toString()
                    def jira_repo_tag = jira_issue?.data?.fields?.customfield_11300?.toString()
                    def jira_sprint_version = jira_issue?.data?.fields?.customfield_10403?.name?.toString()
                    JIRA_ISSUE_CREATER_EMAIL = jira_issue?.data?.fields?.creator?.emailAddress?.toString()
                    def test_env_build_command = yaml_datas?.test_env_deploy?.build_command?.toString()
                    if(!test_env_build_command){
                        jiraComment(body: '未配置测试环境发布参数，流水线发布结束。',issueKey: JIRA_ISSUE_KEY)
                        error("未配置测试环境发布参数，流水线发布结束。")
                    }
                    if (jira_repo_tag?.toLowerCase()?.contains('hotfix') ||jira_issue_summary.contains("紧急修复")) {
                        DEPLOY_VERSION_TYPE = "HOTFIX"
                    }else {
                        def current_version_number = []
                        def service_version_check_cmd = yaml_datas?.health_check_command?.toString()
                        def service_version_check_url = yaml_datas?.service_health_check_url?.toString()
                        if (!(service_version_check_cmd && service_version_check_url)) {
                            DEPLOY_VERSION_TYPE = "BIG"
                        }else {
                            def response_datas = sh(returnStdout: true, script: service_version_check_cmd + service_version_check_url)
                            echo("健康检查链接请求响应数据："+ response_datas?.toString())
                            if (response_datas) {
                                def json_datas = readJSON(file: '', text: response_datas)
                                if (json_datas?.version?.toString()) {
                                    //匹配出版本号，存储在current_version_number中
                                    json_datas?.version?.toString()?.eachMatch('\\d+') {current_num->
                                        current_version_number.add(current_num)
                                    }
                                }else {
                                    error("服务响应返回非json格式数据或响应数据中未包含版本信息")
                                }
                            }else {
                                DEPLOY_VERSION_TYPE = "BIG"
                            }
                        }
                        def target_version_number = []
                        def version_pattern = "\\d+"
                        def match_tag_version = jira_repo_tag =~ version_pattern
                        def match_jira_version = jira_sprint_version =~ version_pattern
                        while(match_tag_version.find()){
                            target_version_number.add(match_tag_version.group())
                        }
                        while (match_jira_version.find()){
                            target_version_number.add(match_jira_version.group())
                        }
                        if(!(match_tag_version.find() || match_jira_version.find())){
                            DEPLOY_VERSION_TYPE = "BIG"
                        }
                        if(target_version_number.size()>= 3){
                            if(target_version_number[0] == current_version_number[0] && target_version_number[1] == current_version_number[1]){
                                DEPLOY_VERSION_TYPE = "SMALL"
                            }else{
                                DEPLOY_VERSION_TYPE = "BIG"
                            }
                        }else{
                            DEPLOY_VERSION_TYPE = "BIG"
                        }
                    }
                    jiraComment(body: "jenkins流水线(pipeline)发布启动成功",issueKey:"${JIRA_ISSUE_KEY}")
                    if (params.JIRA_ISSUE_STATUS == "进行中") {
                        hook = registerWebhook()
                        jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                        if (DEPLOY_VERSION_TYPE == "HOTFIX") {
                            retry(3){
                                step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "现网热修复"])
                            }
                        }else {
                            retry(3){
                                step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "转测试负责人"])
                            }
                        }
                        data = waitForWebhook hook
                        JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                        if(JIRA_ISSUE_STATUS_PARAM == "待需求评审"){
                            stage('需求评审确认'){
                                script{
                                    hook = registerWebhook()
                                    jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                                    data = waitForWebhook hook
                                    JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                    if(JIRA_ISSUE_STATUS_PARAM == "需求评审已通过"){
                                        stage ('UED设计评审确认') {
                                            script {
                                                hook = registerWebhook()
                                                jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                                                if (DEPLOY_VERSION_TYPE == "SMALL") {
                                                    retry(3){
                                                        step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "小版本发布"])
                                                    }
                                                }else if (DEPLOY_VERSION_TYPE == "BIG") {
                                                    retry(3){
                                                        step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "大版本发布"])
                                                    }
                                                }
                                                data = waitForWebhook hook
                                                JIRA_ISSUE_STATUS_PARAM = data.trim().toString()
                                                if (JIRA_ISSUE_STATUS_PARAM == "待架构评审") {
                                                    stage ('架构评审') {
                                                        hook = registerWebhook()
                                                        jiraComment body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY
                                                        data = waitForWebhook hook
                                                        JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                                        if(JIRA_ISSUE_STATUS_PARAM == "架构评审已通过"){
                                                            stage ('数据库操作评审'){
                                                                script {
                                                                    hook = registerWebhook()
                                                                    jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                                                                    data = waitForWebhook hook
                                                                    JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                                                    if(JIRA_ISSUE_STATUS_PARAM == "数据库操作评审已通过"){
                                                                        stage ('测试用例评审') {
                                                                            script {
                                                                                hook = registerWebhook()
                                                                                jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                                                                                retry(3){
                                                                                    step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "推送部署"])
                                                                                }
                                                                                data = waitForWebhook hook
                                                                                JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }else if(JIRA_ISSUE_STATUS_PARAM == "架构评审已通过"){
                                                    stage ('数据库操作评审'){
                                                        script {
                                                            hook = registerWebhook()
                                                            jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                                                            data = waitForWebhook hook
                                                            JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                                            if(JIRA_ISSUE_STATUS_PARAM == "数据库操作评审已通过"){
                                                                stage ('测试用例评审') {
                                                                    script {
                                                                        hook = registerWebhook()
                                                                        jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                                                                        retry(3){
                                                                            step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "推送部署"])
                                                                        }
                                                                        data = waitForWebhook hook
                                                                        JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        stage ('测试环境发布') {
            //根据实际情况设置jenkins 构建代理服务器
            agent {
                label "jenkins-s-gs-test"
            }
            when {
                expression {
                    (JIRA_ISSUE_STATUS_PARAM == "测试用例评审已通过" || JIRA_ISSUE_STATUS_PARAM == "进行中" || params.JIRA_ISSUE_STATUS == "测试环境") && currentBuild.currentResult == "SUCCESS"
                }
            }
            steps {
                script {
                    def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
                    def tedshcu = yaml_datas?.test_env_deploy?.dependency_service_health_check_url?.toString()
                    def tedsv = yaml_datas?.test_env_deploy?.dependency_service_version?.toString()
                    def tehcc = yaml_datas?.health_check_command?.toString()
                    def test_env_build_cmd = yaml_datas?.test_env_deploy?.build_command?.toString()
                    def remote_server_name = yaml_datas?.test_env_deploy?.deploy_server_name?.toString()
                    def published_file = yaml_datas?.test_env_deploy?.deploy_file_name?.toString()
                    def exec_command = yaml_datas?.test_env_deploy?.deploy_command_ssh?.toString()
                    def published_remote_path = yaml_datas?.test_env_deploy?.deploy_remote_path?.toString()
                    def remove_prefix_path = yaml_datas?.test_env_deploy?.remove_prefix_path?.toString()
                    def excludes_file = yaml_datas?.test_env_deploy?.excludes_file?.toString()
                    def test_deploy_cmd = yaml_datas?.test_env_deploy?.deploy_command?.toString()
                    if (tedshcu && tedsv) {
                        def check_result = sh(returnStdout: true, script: tehcc + tedshcu)
                        check_result = readJSON(file: "",text: check_result)
                        if (check_result?.version?.toString() != tedsv) {
                            jiraComment(body: "测试环境发布失败，请检查依赖服务。", issueKey: JIRA_ISSUE_KEY)
                            error("测试环境发布失败，请检查依赖服务。")
                        }
                    }
                    if (test_env_build_cmd) {
                        withSonarQubeEnv('SonarQube') {
                            def build_result = sh(returnStatus: true, script: "${test_env_build_cmd}")
                            if (build_result != 0) {
                                currentBuild.result = "FAILURE"
                                error("编译出错或者代码扫描不通过，请检查源代码和编译命令")
                            }
                        }
                    }

                    if (remote_server_name && published_file && exec_command && published_remote_path) {
                        sshPublisher(failOnError: true,publishers: [sshPublisherDesc(configName: remote_server_name, sshRetry: [retries: 5, retryDelay: 10000], transfers: [sshTransfer(excludes: excludes_file, execCommand: exec_command, execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: published_remote_path, remoteDirectorySDF: false, removePrefix: remove_prefix_path, sourceFiles: published_file)], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                        sleep(1)
                        retry(3){
                            step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "提交测试"])
                        }
                    }
                    //如果填写发布命令，则执行发布命令
                    if (test_deploy_cmd) {
                        def deploy_result = sh(returnStatus: true, script:"${test_deploy_cmd}")
                        if (deploy_result != 0) {
                            currentBuild.result = "FAILURE"
                            error("测试环境发布失败，请检查发布命令和配置信息")
                        }
                    }
                    //判断是否配置测试环境自动化测试、演示环境编译参数
                    if(!(yaml_datas?.test_env_deploy?.autotest_jenkins_link || yaml_datas?.test_env_deploy?.autotest_script || yaml_datas?.demo_env_deploy?.build_command || yaml_datas?.perfor_env_deploy?.build_command)){
                        emailext(
                                attachLog: true,
                                body: "您的JIRA上线申请单${env.JIRA_ISSUE_KEY}【测试环境】流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
                                subject: "JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
                                //修改构建失败邮件收件人
                                to: JIRA_ISSUE_CREATER_EMAIL
                        )
                        jiraComment(body: "未配置测试环境自动化测试参数、压测环境发布命令和演示环境发布命令，jenkins流水线(pipeline)发布结束。",issueKey: JIRA_ISSUE_KEY)
                        currentBuild.result = "ABORTED"
                        error("未配置测试环境自动化测试参数、压测环境发布命令和演示环境发布命令，jenkins流水线(pipeline)发布结束。")
                    }
                }
            }
        }
        stage ('测试环境自动化测试'){
            agent{
                label 'master'
            }
            when {
                expression {
                    (params.JIRA_ISSUE_STATUS == "测试环境" || JIRA_ISSUE_STATUS_PARAM == "进行中" || JIRA_ISSUE_STATUS_PARAM == "测试用例评审已通过") && currentBuild.currentResult == "SUCCESS"
                }
            }
            steps {
                script {
                    def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
                    def test_job = yaml_datas?.test_env_deploy?.autotest_jenkins_link?.toString()
                    def trigger_token = yaml_datas?.test_env_deploy?.autotest_trigger_token?.toString()
                    def should_not_fail_build = yaml_datas?.test_env_deploy?.not_fail_deploy
                    def jira_issue = jiraGetIssue idOrKey: JIRA_ISSUE_KEY, site: "JIRA"
                    JIRA_ISSUE_CREATER_EMAIL = jira_issue?.data?.fields?.creator?.emailAddress?.toString()
                    def jira_small_version_deploy_field_value = jira_issue?.data?.fields?.customfield_11400?.value?.toString()
                    def autotest_script = yaml_datas?.test_env_deploy?.autotest_script?.toString()
                    if (test_job && trigger_token && should_not_fail_build) {
                        def handle = triggerRemoteJob(abortTriggeredJob: true, enhancedLogging: true, job: test_job, maxConn: 1, pollInterval: 30, remoteJenkinsName: 'remoteJenkins', shouldNotFailBuild: should_not_fail_build, token: trigger_token, useCrumbCache: true, useJobInfoCache: true)
                    }
                    if (autotest_script) {
                        def autotest_result = sh(returnStatus: true,script: autotest_script)
                        if (autotest_result !=0){
                            currentBuild.result = "FAILURE"
                            error("自动化测试/代码扫描不通过")
                        }
                    }
                    jiraComment(body: "测试环境自动化测试完成，请确认测试环境手工测试。", issueKey: JIRA_ISSUE_KEY)
                    emailext(
                            attachLog: true,
                            body: "您的JIRA上线申请单${env.JIRA_ISSUE_KEY}【测试环境】流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
                            subject: "JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
                            //修改构建失败邮件收件人
                            to: JIRA_ISSUE_CREATER_EMAIL
                    )
                    hook = registerWebhook()
                    jiraComment(body: "Webhook:${hook.getURL()}", issueKey: JIRA_ISSUE_KEY)
                    data = waitForWebhook hook
                    JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                    if (JIRA_ISSUE_STATUS_PARAM == "待产品确认") {
                        hook = registerWebhook()
                        jiraComment(body: "Webhook:${hook.getURL()}",issueKey: JIRA_ISSUE_KEY)
                        retry(3){
                            step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "推送部署"])
                        }
                        data = waitForWebhook hook
                        JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                    }
                    if (JIRA_ISSUE_STATUS_PARAM == "Rejected") {
                        step([$class: 'WsCleanup'])
                        currentBuild.result = "ABORTED"
                        error("测试/审批不通过，结束此次发布")
                    } else if (JIRA_ISSUE_STATUS_PARAM == "待UED验收") {
                        if (DEPLOY_VERSION_TYPE == "SMALL") {
                            retry(3){
                                step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "小版本发布"])
                            }
                            hook = registerWebhook()
                            jiraComment(body: "Webhook:${hook.getURL()}",issueKey: JIRA_ISSUE_KEY)
                            data = waitForWebhook hook
                            JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                            if (JIRA_ISSUE_STATUS_PARAM == "Rejected") {
                                step([$class: 'WsCleanup'])
                                currentBuild.result = "ABORTED"
                                error("测试/审批不通过，结束此次发布")
                            } else if (JIRA_ISSUE_STATUS_PARAM == "待产品确认") {
                                hook = registerWebhook()
                                jiraComment(body: "Webhook:${hook.getURL()}",issueKey: JIRA_ISSUE_KEY)
                                retry(3){
                                    step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "推送部署"])
                                }
                                data = waitForWebhook hook
                                JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                            }
                        } else if (DEPLOY_VERSION_TYPE == "BIG" || (DEPLOY_VERSION_TYPE == "SMALL" && jira_small_version_deploy_field_value == "是")) {
                            retry(3){
                                step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "大版本发布"])
                                sleep(1)
                                step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "推送部署"])
                            }
                            //压测环境部署和性能测试
                            node('master') {
                                stage('压测环境发布') {
                                    def pedshcu = yaml_datas?.perfor_env_deploy?.dependency_service_health_check_url?.toString()
                                    def pedsv = yaml_datas?.perfor_env_deploy?.dependency_service_version?.toString()
                                    def pehcc = yaml_datas?.health_check_command?.toString()
                                    def pebc = yaml_datas?.perfor_env_deploy?.build_command?.toString()
                                    def prsn = yaml_datas?.perfor_env_deploy?.deploy_server_name?.toString()
                                    def ppf = yaml_datas?.perfor_env_deploy?.deploy_file_name?.toString()
                                    def pec = yaml_datas?.perfor_env_deploy?.deploy_command_ssh?.toString()
                                    def pprp = yaml_datas?.perfor_env_deploy?.deploy_remote_path?.toString()
                                    def prpp = yaml_datas?.perfor_env_deploy?.remove_prefix_path?.toString()
                                    def pef = yaml_datas?.perfor_env_deploy?.excludes_file?.toString()
                                    def pdc = yaml_datas?.perfor_env_deploy?.deploy_command?.toString()
                                    if (pedshcu && pedsv) {
                                        def check_result = sh(returnStdout: true, script: pehcc + pedshcu)
                                        check_result = readJSON(file: "",text: check_result)
                                        if (check_result?.version?.toString() !=pedsv) {
                                            jiraComment(body: "压测环境发布失败，请检查依赖服务。", issueKey: JIRA_ISSUE_KEY)
                                            error("压测环境发布失败，请检查依赖服务。")
                                        }
                                    }
                                    if (pebc) {
                                        def build_result = sh(returnStatus: true, script: "${pebc}")
                                        if (build_result != 0) {
                                            currentBuild.result = "FAILURE"
                                            error("编译出错，请检查源代码和编译命令")
                                        }
                                    }
                                    if (prsn && ppf && pec && pprp) {
                                        sshPublisher(failOnError: true,publishers: [sshPublisherDesc(configName: prsn, sshRetry: [retries: 5, retryDelay: 10000], transfers: [sshTransfer(excludes: pef, execCommand: pec, execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: pprp, remoteDirectorySDF: false, removePrefix: prpp, sourceFiles: ppf)], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                                    }
                                    //如果填写发布命令，则执行发布命令
                                    if (pdc) {
                                        def perfor_deploy_result = sh(returnStatus: true, script:"${pdc}")
                                        if (perfor_deploy_result != 0) {
                                            currentBuild.result = "FAILURE"
                                            error("压测环境发布失败，请检查发布命令和配置信息")
                                        }
                                    }
                                }
                                stage('性能测试') {
                                    def jiraIssue = jiraGetIssue(idOrKey: JIRA_ISSUE_KEY, site: 'JIRA')
                                    JIRA_ISSUE_CREATER_EMAIL = jiraIssue?.data?.fields?.creator?.emailAddress?.toString()
                                    def perfor_job = yaml_datas?.perfor_env_deploy?.autotest_jenkins_link?.toString()
                                    def perfor_trigger_token = yaml_datas?.perfor_env_deploy?.autotest_trigger_token?.toString()
                                    def psnfd = yaml_datas?.perfor_env_deploy?.not_fail_deploy
                                    def perfor_autotest_script = yaml_datas?.perfor_env_deploy?.autotest_script?.toString()
                                    if (perfor_autotest_script) {
                                        def perfor_result = sh(returnStatus: true,script: perfor_autotest_script)
                                    }
                                    if (perfor_job && perfor_trigger_token && psnfd) {
                                        def handle = triggerRemoteJob(abortTriggeredJob: true, enhancedLogging: true, job: perfor_job, maxConn: 1, pollInterval: 30, remoteJenkinsName: 'remoteJenkins', shouldNotFailBuild: psnfd, token: perfor_trigger_token, useCrumbCache: true, useJobInfoCache: true)
                                    }
                                    jiraComment(body: "压测环境性能测试完成，请确认性能测试结论。",issueKey: JIRA_ISSUE_KEY)
                                    /*
                                    emailext(
                                            attachLog: true,
                                            body: "您的JIRA上线申请单${env.JIRA_ISSUE_KEY}【压测环境】流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
                                            subject: "JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
                                            //修改构建失败邮件收件人
                                            to: JIRA_ISSUE_CREATER_EMAIL
                                    )
                                    */
                                    hook = registerWebhook()
                                    jiraComment(body: "Webhook:${hook.getURL()}",issueKey: JIRA_ISSUE_KEY)
                                    data = waitForWebhook hook
                                    JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                    if (JIRA_ISSUE_STATUS_PARAM == "Rejected") {
                                        step([$class: 'WsCleanup'])
                                        currentBuild.result = "ABORTED"
                                        error("测试/审批不通过，结束此次发布")
                                    } else if (JIRA_ISSUE_STATUS_PARAM == "待产品确认") {
                                        hook = registerWebhook()
                                        jiraComment(body: "Webhook:${hook.getURL()}",issueKey: JIRA_ISSUE_KEY)
                                        sleep(1)
                                        retry(3){
                                            step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "推送部署"])
                                        }
                                        data = waitForWebhook hook
                                        JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        stage ('演示环境发布') {
            //根据实际情况设置jenkins 代理
            agent {
                label "jenkins-s-aly-demo"
            }
            when {
                expression {
                    (JIRA_ISSUE_STATUS_PARAM == "待部署 - 演示"  || JIRA_ISSUE_STATUS_PARAM == "演示环境" || JIRA_ISSUE_STATUS_PARAM =="待产品确认") && currentBuild.currentResult == "SUCCESS"
                }
            }
            steps {
                script {
                    //从yaml文件读取数据
                    def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
                    def dedshcu = yaml_datas?.demo_env_deploy?.dependency_service_health_check_url?.toString()
                    def dedsv = yaml_datas?.demo_env_deploy?.dependency_service_version?.toString()
                    def demo_health_check_cmd = yaml_datas?.health_check_command?.toString()
                    def demo_env_build_cmd = yaml_datas?.demo_env_deploy?.build_command?.toString()
                    def remote_server_name = yaml_datas?.demo_env_deploy?.deploy_server_name?.toString()
                    def published_file = yaml_datas?.demo_env_deploy?.deploy_file_name?.toString()
                    def exec_command = yaml_datas?.demo_env_deploy?.deploy_command_ssh?.toString()
                    def published_remote_path = yaml_datas?.demo_env_deploy?.deploy_remote_path?.toString()
                    def remove_prefix_path = yaml_datas?.demo_env_deploy?.remove_prefix_path?.toString()
                    def excludes_file = yaml_datas?.demo_env_deploy?.excludes_file?.toString()
                    def demo_deploy_cmd = yaml_datas?.demo_env_deploy?.deploy_command?.toString()
                    //检查服务依赖，项目部署其他依赖服务请设置依赖服务健康检查链接和依赖服务版本
                    if (dedshcu && dedsv) {
                        def check_result = sh(returnStdout: true,script: demo_health_check_cmd + dedshcu)
                        check_result = readJSON(file: "",text: check_result)
                        if (check_result?.version?.toString() != dedsv) {
                            jiraComment(body: "演示环境发布失败，请检查依赖服务。",issueKey: JIRA_ISSUE_KEY)
                            error("演示环境发布失败，请检查依赖服务。")
                        }
                    }
                    //在jenkins上通过shell命令调用maven/nodejs进行打包编译
                    if (demo_env_build_cmd) {
                        def demo_build_result = sh(returnStatus: true,script:"${demo_env_build_cmd}")
                        if (demo_build_result !=0) {
                            currentBuild.result = "FAILURE"
                            error("构建失败，请检查构建命令和源代码")
                        }
                    }
                    //将打包编译包推送到目录服务器
                    if (remote_server_name && published_file && exec_command && published_remote_path) {
                        sshPublisher(failOnError: true,publishers: [sshPublisherDesc(configName: remote_server_name,sshRetry: [retries: 5, retryDelay: 10000],transfers: [sshTransfer(excludes: excludes_file, execCommand: exec_command, execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: published_remote_path, remoteDirectorySDF: false, removePrefix: remove_prefix_path, sourceFiles: published_file)], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                    }
                    //如果填写发布命令，则执行发布命令
                    if (demo_deploy_cmd) {
                        def deploy_result = sh(returnStatus: true,script:demo_deploy_cmd)
                        if (deploy_result != 0) {
                            currentBuild.result = "FAILURE"
                            error("发布失败，请检查发布命令和配置信息。")
                        }
                    }
                }
            }
        }
        stage ('演示环境自动化测试') {
            agent {
                label "master"
            }
            when {
                expression {
                    (JIRA_ISSUE_STATUS_PARAM == "待部署 - 演示" || JIRA_ISSUE_STATUS_PARAM == "演示环境" || JIRA_ISSUE_STATUS_PARAM =="待产品确认") && currentBuild.currentResult == "SUCCESS"
                }
            }
            steps {
                script {
                    //从yaml文件读取数据
                    def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
                    def demo_job = yaml_datas?.demo_env_deploy?.autotest_jenkins_link?.toString()
                    def trigger_token = yaml_datas?.demo_env_deploy?.autotest_trigger_token?.toString()
                    def should_not_fail_build = yaml_datas?.demo_env_deploy?.not_fail_deploy
                    def demo_autotest_script = yaml_datas?.demo_env_deploy?.autotest_script?.toString()
                    def jira_issue = jiraGetIssue(idOrKey: JIRA_ISSUE_KEY, site: 'JIRA')
                    JIRA_ISSUE_CREATER_EMAIL = jira_issue?.data?.fields?.creator?.emailAddress?.toString()
                    if (demo_job && trigger_token && should_not_fail_build) {
                        triggerRemoteJob(abortTriggeredJob: true,enhancedLogging: true,job: demo_job,maxConn: 1,pollInterval: 30,remoteJenkinsName: 'remoteJenkins', shouldNotFailBuild: should_not_fail_build, token: trigger_token, useCrumbCache: true, useJobInfoCache: true)
                    }
                    if (demo_autotest_script) {
                        def demo_result = sh(returnStatus: true,script: demo_autotest_script)
                    }
                    jiraComment(body: "演示环境自动化测试完成，请确认演示环境手工测试。",issueKey: JIRA_ISSUE_KEY)
                    emailext(
                            attachLog: true,
                            body: "您的JIRA上线申请单${env.JIRA_ISSUE_KEY}【演示环境】流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
                            subject: "JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
                            //修改构建失败邮件收件人
                            to: JIRA_ISSUE_CREATER_EMAIL
                    )
                    hook = registerWebhook()
                    jiraComment(body: "Webhook:${hook.getURL()}",issueKey: JIRA_ISSUE_KEY)
                    data = waitForWebhook hook
                    JIRA_ISSUE_STATUS_PARAM = data.trim().toString()
                    if (JIRA_ISSUE_STATUS_PARAM == "Rejected") {
                        step([$class: 'WsCleanup'])
                        currentBuild.result = "ABORTED"
                        error("测试/审批不通过，结束此次发布")
                    }else if (JIRA_ISSUE_STATUS_PARAM == "待审批") {
                        hook = registerWebhook()
                        jiraComment(body: "Webhook:${hook.getURL()}",issueKey: JIRA_ISSUE_KEY)
                        //注释 生产环境自动发布
                        //step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "推送部署"])
                        data = waitForWebhook hook
                        JIRA_ISSUE_STATUS_PARAM = data?.trim()?.toString()
                    }
                }
            }
        }
        stage ('生产环境发布') {
            agent{
                label 'jenkins-s-aly-prod'
            }
            when {
                expression {
                    (JIRA_ISSUE_STATUS_PARAM == "待部署 - 生产" || JIRA_ISSUE_STATUS_PARAM == "待审批") && currentBuild.currentResult == "SUCCESS"
                }
            }
            steps {
                script {
                    def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
                    def cedshcu = yaml_datas?.customer_env_deploy?.dependency_service_health_check_url?.toString()
                    def cedsv = yaml_datas?.customer_env_deploy?.dependency_service_version?.toString()
                    def customer_health_check_cmd = yaml_datas?.health_check_command?.toString()
                    def customer_env_build_cmd = yaml_datas?.customer_env_deploy?.build_command?.toString()
                    def remote_server_name = yaml_datas?.customer_env_deploy?.deploy_server_name?.toString()
                    def published_file = yaml_datas?.customer_env_deploy?.deploy_file_name?.toString()
                    def exec_command = yaml_datas?.customer_env_deploy?.deploy_command_ssh?.toString()
                    def published_remote_path = yaml_datas?.customer_env_deploy?.deploy_remote_path?.toString()
                    def remove_prefix_path = yaml_datas?.customer_env_deploy?.remove_prefix_path?.toString()
                    def excludes_file = yaml_datas?.customer_env_deploy?.excludes_file?.toString()
                    def customer_deploy_cmd = yaml_datas?.customer_env_deploy?.deploy_command?.toString()
                    if (cedshcu && cedsv) {
                        def check_result = sh(returnStdout: true,script: customer_health_check_cmd + cedshcu)
                        check_result = readJSON file: '', text: check_result
                        if (check_result?.version?.toString() != cedsv) {
                            jiraComment(body: "生产环境发布失败，请检查依赖服务。",issueKey: JIRA_ISSUE_KEY)
                            error("生产环境发布失败，请检查依赖服务。")
                        }
                    }
                    //在jenkins上通过shell命令调用maven/nodejs进行打包编译
                    if (customer_env_build_cmd) {
                        def customer_build_result = sh(returnStatus: true,script: customer_env_build_cmd)
                        if (customer_build_result != 0) {
                            error("构建失败，请检查构建命令和源代码")
                        }
                    }
                    if (remote_server_name && published_file && exec_command && published_remote_path) {
                        sshPublisher(failOnError: true,publishers: [sshPublisherDesc(configName: remote_server_name,sshRetry: [retries: 5, retryDelay: 10000],transfers: [sshTransfer(excludes: excludes_file, execCommand: exec_command, execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: published_remote_path, remoteDirectorySDF: false, removePrefix: remove_prefix_path, sourceFiles: published_file)], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                    }
                    if (customer_deploy_cmd) {
                        def customer_deploy_result = sh(returnStatus: true,script: customer_deploy_cmd)
                        if (customer_deploy_result !=0) {
                            error("生产环境发布失败，请检查发布命令和配置信息")
                        }
                    }
                }
            }
        }
        stage ('生产环境自动化测试') {
            agent{
                label 'master'
            }
            when {
                expression {
                    (JIRA_ISSUE_STATUS_PARAM == "待部署 - 生产" || JIRA_ISSUE_STATUS_PARAM == "待审批") && currentBuild.currentResult == "SUCCESS"
                }
            }
            steps {
                script {
                    def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
                    def customer_job = yaml_datas?.customer_env_deploy?.autotest_jenkins_link?.toString()
                    def trigger_token = yaml_datas?.customer_env_deploy?.autotest_trigger_token?.toString()
                    def should_not_fail_build = yaml_datas?.customer_env_deploy?.not_fail_deploy
                    def customer_autotest_script = yaml_datas?.customer_env_deploy?.autotest_script?.toString()
                    def jira_issue = jiraGetIssue(idOrKey: JIRA_ISSUE_KEY, site: 'JIRA')
                    JIRA_ISSUE_CREATER_EMAIL = jira_issue?.data?.fields?.creator?.emailAddress?.toString()
                    if (customer_job && trigger_token && should_not_fail_build) {
                        triggerRemoteJob(abortTriggeredJob: true,enhancedLogging: true,job: customer_job,maxConn: 1, pollInterval: 30, remoteJenkinsName: 'remoteJenkins', shouldNotFailBuild: should_not_fail_build, token: trigger_token, useCrumbCache: true, useJobInfoCache: true)
                    }
                    if (customer_autotest_script) {
                        def customer_autotest_result = sh(returnStatus: true,script: customer_autotest_script)
                    }
                    jiraComment(body: "完成生产环境自动化测试，请验证生产环境。",issueKey: JIRA_ISSUE_KEY)
                    /*
                    emailext(
                            attachLog: true,
                            body: "您的JIRA上线申请单${env.JIRA_ISSUE_KEY}【生产环境】流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
                            subject: "JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
                            //修改构建失败邮件收件人
                            to: JIRA_ISSUE_CREATER_EMAIL
                    )
                    */
                }
            }
        }
    }
    post {
        always{
            emailext(
                    attachLog: true,
                    body: "您的JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
                    subject: "JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
                    //修改构建失败邮件收件人
                    to: 'liang.zhang@flaginfo.com.cn'
            )
        }
    }
}