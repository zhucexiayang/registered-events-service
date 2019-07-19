/*
######################################【待配置项】#########################################################
*      1.如果报无法找到Jenkinsfile.yaml，请根据enkinsfile.groovy的实际路径替换"Jenkinsfile.yaml"，默认以项目检出目录为准；
*      2.将文件名修改为"Jenkinsfile.groovy"，jenkins任务构建配置仅执行名为Jenkinsfile.grovy的pipeline
*      3.更多配置说明请访问Confluence( http://confluence.flaginfo.com.cn/pages/viewpage.action?pageId=27011891 )

       Version：2.1
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
		stage ('构建开发环境') {
			agent {
				label "jenkins-s-gs-dev"
			}
			when {
				expression {
					params.JIRA_ISSUE_STATUS == "开发环境"
				}
			}
			steps {
				script {
					def yaml_datas = readYaml(file: "Jenkinsfile.yaml")
					def jira_issue = jiraGetIssue(idOrKey: JIRA_ISSUE_KEY, site: "JIRA")
					JIRA_ISSUE_CREATER_EMAIL = jira_issue?.data?.fields?.creator?.emailAddress?.toString()
					def dev_env_build_cmd = yaml_datas?.dev_env_deploy?.build_command?.toString()
                    def remote_server_name = yaml_datas?.dev_env_deploy?.deploy_server_name?.toString()
                    def published_file = yaml_datas?.dev_env_deploy?.deploy_file_name?.toString()
                    def exec_command = yaml_datas?.dev_env_deploy?.deploy_command_ssh?.toString()
                    def published_remote_path = yaml_datas?.dev_env_deploy?.deploy_remote_path?.toString()
                    def remove_prefix_path = yaml_datas?.dev_env_deploy?.remove_prefix_path?.toString()
                    def excludes_file = yaml_datas?.dev_env_deploy?.excludes_file?.toString()
                    def dev_deploy_cmd = yaml_datas?.dev_env_deploy?.deploy_command?.toString()
					
					if (dev_env_build_cmd) {
						withSonarQubeEnv('SonarQube') {
							def build_result = sh(returnStatus: true, script: "${dev_env_build_cmd}")
							if (build_result != 0) {
								currentBuild.result = "FAILURE"
								step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "返回To Do"])
								jiraComment(body: "【开发环境】编译出错或者代码扫描不通过，请检查源代码和编译命令。",issueKey: JIRA_ISSUE_KEY)
								error("【开发环境】编译出错或者代码扫描不通过，请检查源代码和编译命令")
							}
						}
					}
					if (remote_server_name && published_file && exec_command && published_remote_path) {
						sshPublisher(failOnError: true,publishers: [sshPublisherDesc(configName: remote_server_name, sshRetry: [retries: 5, retryDelay: 10000], transfers: [sshTransfer(excludes: excludes_file, execCommand: exec_command, execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: published_remote_path, remoteDirectorySDF: false, removePrefix: remove_prefix_path, sourceFiles: published_file)], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
					}
					//如果填写发布命令，则执行发布命令
					if (dev_deploy_cmd) {
						def deploy_result = sh(returnStatus: true, script:"${test_deploy_cmd}")
						if (deploy_result != 0) {
							step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "返回To Do"])
							currentBuild.result = "FAILURE"
							jiraComment(body: "【开发环境】发布失败，请检查发布命令和配置信息。",issueKey: JIRA_ISSUE_KEY)
							error("【开发环境】发布失败，请检查发布命令和配置信息")
						}
					}
					emailext(
						attachLog: true,
						body: "您的JIRA上线申请单${env.JIRA_ISSUE_KEY}【开发环境】流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
						subject: "【开发环境】JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
						//修改构建失败邮件收件人
						to: JIRA_ISSUE_CREATER_EMAIL
				    )
					step([$class: 'JiraIssueUpdateBuilder', jqlSearch: "issuekey = ${JIRA_ISSUE_KEY}", workflowActionName: "返回To Do"])
				    jiraComment(body: "【开发环境】Pipline流水线发布结束。",issueKey: JIRA_ISSUE_KEY)
				    currentBuild.result = "SUCCESS"
				}
			}
			
		}
   }
   post {
	   always{
		   emailext(
				   attachLog: true,
				   body: "【开发环境】您的JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线发布结果：${currentBuild.currentResult}，详细发布日志请查看附件。",
				   subject: "【开发环境】JIRA上线申请单${env.JIRA_ISSUE_KEY}流水线(pipeline)发布通知",
				   //修改构建失败邮件收件人
				   to: JIRA_ISSUE_CREATER_EMAIL
		   )
	   }
   }
}