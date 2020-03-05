import React from 'react';
import {  HashLink as Link  } from 'react-router-hash-link';
import './Help.scss';
import graph from '../../assets/help/EMR_Graph.png';
import clusterDetails from '../../assets/help/Cluster_Details_1.png';
import clusterDetails1 from '../../assets/help/cluster_details.gif';
import expertAdvice from '../../assets/help/Expert_Advice.png';
import workflow from '../../assets/help/ViewWorkflow.png';
import createClusterWf from '../../assets/help/CreateClusterWorkflow.png';
import createCluster from '../../assets/help/Create_Cluster.gif';
import ERDiag from '../../assets/help/QuickFabric_ER_Diagram.png';
import admin from '../../assets/help/Admin.png';
import profile from '../../assets/help/UserProfile.png';
import Sandeep from '../../assets/help/suttamchandani.jpg';
import Giri from '../../assets/help/gbagdi.jpg';
import Khilawar from '../../assets/help/kverma.jpg';
import Gaurav from '../../assets/help/gdoon.jpg';
import Nisha from '../../assets/help/nkk.jpg';
import Dan from '../../assets/help/drussotto.jpg';
import Varun from '../../assets/help/vsood.jpg';
import Kevin from '../../assets/help/ktran7.jpg';
import Architecture from '../../assets/help/architecture.png';
import AddSteps from '../../assets/help/addSteps.gif';
import AutoPilot from '../../assets/help/autoPilot.gif';
import CloneCluster from '../../assets/help/cloneCluster.gif';
import DNSFlip from '../../assets/help/dnsFlip.gif';
import RotateAMI from '../../assets/help/rotateAMI.gif';
import TerminateCluster from '../../assets/help/terminateCluster.gif';
import AccountSetup from '../../assets/help/AccountSetup.gif';
import AccountConfig from '../../assets/help/AccountConfig.png';
import AppConfig from '../../assets/help/AppConfig.png';
import EMRCost from '../../assets/help/EMRCost.png';
import ClusterCost from '../../assets/help/ClusterCost.png';

/**
 * Help page for the platform.
 * Images and gifs to show how to perform different functionalities.
 */
export default class Help extends React.Component {
    constructor(props) {
        super(props);

    }
    render() {
        return (
            <div className='help-container'>
                <h2>Help Page</h2>
                <div className="help-list">
                    <p>What is on this page:</p>
                    <ol>
                        <li className="list-1"><Link smooth to="/help#about">About</Link></li>
                        <ul>
                            <li className="list-2"><Link smooth to="/help#overview">Overview</Link></li>
                            {/* <li className="list-2"><Link smooth to="/help#why">Why</Link></li> */}
                        </ul>
                        <li className="list-1"><Link smooth to="/help#details">Framework Details</Link></li>
                        <li className="list-1"><Link smooth to="/help#architecture">Overall Architecture</Link></li>
                        <li className="list-1"><Link smooth to="/help#dbERdiag">Database Entity-Relationship Diagram</Link></li>
                        <li className="list-1"><Link smooth to="/help#gettingStarted">Getting Started</Link></li>
                        <ul>
                            <li className="list-2"><Link smooth to="/help#accountSetup">Account Setup</Link></li>
                            <li className="list-2"><Link smooth to="/help#configs">Key Configurations</Link></li>
                        </ul>
                        <li className="list-1"><Link smooth to="/help#features">Features</Link></li>
                        <ul>
                            <li className="list-2"><Link smooth to="/help#observability">EMR Observability</Link></li>
                            <li className="list-2"><Link smooth to="/help#orchestration">EMR Orchestration</Link></li>
                            <li className="list-2"><Link smooth to="/help#cost">EMR Cost</Link></li>
                            <li className="list-2"><Link smooth to="/help#access">Access Control</Link></li>
                            <li className="list-2"><Link smooth to="/help#admin">Administration</Link></li>
                            <li className="list-2"><Link smooth to="/help#profile">User Profile</Link></li>
                        </ul>
                        {/* <li className="list-1"><Link smooth to="/help#subscription">Report Subscriptions</Link></li> */}
                        <li className="list-1"><Link smooth to="/help#notification">Email Notification</Link></li>
                        <li className="list-1"><Link smooth to="/help#videos">Accolades</Link></li>
                        <li className="list-1"><Link smooth to="/help#team">Team</Link></li>
                    </ol>       
                
                <div className="help-list-content">
                    <div className="scroll" id="about">
                        <h2 style={{ color: '#0097E4' }}>About</h2>
                    </div>
                    <div className="scroll" id="overview">
                        <h3 style={{ color: '#0097E4' }}>Overview</h3>
                        <p>
                            How do companies scale to run over 100K pipelines every day? QuickFabric is the answer to managing EMR clusters 
                            in an automated fashion requiring zero human intervention for monitoring, 
                            configuration, change management, availability, quality, security, cost. It provides key status metrics and operational 
                            insights enabling customers a one stop shop for all things EMR. The target audience for QuickFabric is Big Data Engineers, 
                            Data Analysts, and Data Leaders, enabling enterprise-scale data deployments and leading to enable data analysts 
                            and scientists to get more insights.
                        </p>
                    </div>
                    <div className="scroll" id="details">
                        <h2 style={{ color: '#0097E4' }}>Framework Details</h2>
                        <table border='1'>
                            <tr>
                                <th bgcolor='#ede993'><p>Tech Stack</p></th>
                                <th bgcolor='#ede993'><p>Security Features</p></th>
                                <th bgcolor='#ede993'><p>Java Backend Features</p></th>
                            </tr>
                            <tr>
                                <td style={{ verticalAlign: "top"}}>
                                        <p>React</p>
                                        <p>Redux</p>
                                        <p>Java/J2ee (Java 8)</p>
                                        <p>Spring Boot</p>
                                        <p>AWS RDS (MySQL)</p>
                                        <p>AWS S3</p>
                                        <p>AWS Serverless framework (API gateway, lambda, python, step functions)</p>
                                        <p>Rest APIs</p>
                                        <p>Scala</p>
                                        <p>AWS EC2, LB/Route53</p>
                                        <p>Tomcat</p>
                                        <p>Nginx</p>
                                        <p>Swagger</p>
                                    
                                </td>
                                <td style={{ verticalAlign: "top"}}>
                                        <p>Single-Sign-On (SSO) enabled for UI authentication</p>
                                        <p>Tool Internal Login</p>
                                        <p>JWT token for API authentication</p>
                                        <p>Inbuilt "Role Based Authorization" functionality</p>
                                        <p>Spring Security enabled</p>
                                        <p>SSL Enabled</p>
                                        <p>Systematic logging</p>
                                </td>
                                <td style={{ verticalAlign: "top"}}>
                                        <p>SpringBoot based Rest API Architecture</p>
                                        <p>Common Exception module</p>
                                        <p>Common Logging Framework (log4j)</p>
                                        <p>Swagger for API documentation</p>
                                        <p>JWT token and internal role based authentication anf authorization for Rest APIs</p>
                                        <p>Annotation based configuration</p>
                                        <p>Constant classes for important variables & 3rd party URLs</p>
                                        <p>Modularize coding practice</p>
                                        <p>Spring Security enabled</p>
                                        <p>SSO Authentication feature</p>
                                        <p>Internal Dev Login feature</p>
                                        <p>Data Access layer enabled for database related operations</p>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div className="scroll" id="architecture">
                        <h2 style={{ color: '#0097E4' }}>Overall Architecture</h2>
                        <img className='help-images' src={Architecture} width="80%" height="80%" />
                    </div>
                    <div className="scroll" id="dbERdiag">
                        <h2 style={{ color: '#0097E4' }}>Database Entity-Relationship Diagram</h2>
                        <img className='help-images' src={ERDiag} width="80%" height="80%" />
                    </div>
                    <div className="scroll" id="gettingStarted">
                        <h2 style={{ color: '#0097E4' }}>Getting Started</h2>
                    </div>
                    <div className="scroll" id="accountSetup">
                        <h3 style={{ color: '#0097E4' }}>Account Setup</h3>
                        <p>
                            Once the code is deployed and running the next task is to create an AWS account and add users to it. 
                            To achieve this, the Account Onboarding section is split into multiple subsection as follows:
                            <p className='subsection'>
                                <br />
                                <h4>1. Account Details</h4>
                                <p className='subsubsection'>
                                    <b>Account ID: </b>Provide an ID for the account. This consists of only integer values.
                                    <br />
                                    <b>Owner Name: </b>Provide an owner for the account.
                                    <br />
                                    <b>Account Environment: </b>Provide what the environment where the account will be used: 
                                    dev for local testing, preprod for regression testing, or production.
                                </p>
                                <h4>2. Segments</h4>
                                <p className='subsubsection'>
                                    Segments define the area where the cluster will be used. It is a logical separation to understand 
                                    which category does the cluster fall into based on the requirements. Segments are independent from 
                                    the account, a cluster can be created for any segment and in any account. In this section a user can 
                                    provide multiple segments. Existing segments are also present in the dropdown to be used which auto 
                                    populates the owner and the email ID fields, or new segment can be created here.
                                </p>
                                <h4>3. Test Suite</h4>
                                <p className='subsubsection'>
                                    Select the tests to be run on the clusters after creation. 
                                    Currently two following tests are available to be run on the clusters:
                                    <br/>
                                    <b>Auto-Scaling: </b>Specify the min and max number of nodes. Here the min and max values will be validated 
                                    against the auto-scaling min and max values provided during cluster creation.
                                    <br/>
                                    <b>No. of Bootstraps: </b>Specify the number bootstrap scripts to be run with the cluster creation. The number of
                                    bootstrap scripts will be validated against the number of bootstrap scripts provided while creating a cluster.
                                </p>
                                <h4>4. Config</h4>
                                <p className='subsubsection'>
                                    Provide the gateway API Key and backend API URL, which is received after server less setup.
                                    <br />
                                    <b>Gateway API Key: </b>Provide the API key received from Terraform after setting up the serverless.
                                    <br />
                                    <b>Backend API URL: </b>Provide the API URL received from Terraform after setting up the serverless.
                                    <br />
                                    <b>Enable JIRA</b>: Set whether you want JIRA configured for this account.
                                </p>
                                <h4>5. New User</h4>
                                <p className='subsubsection'>
                                    Add new users to the account. Specify their email ID, first and last name, what actions they can perform 
                                    on the cluster and which segments they have access to.
                                </p>
                                <h4>6. Review</h4>
                                <p className='subsubsection'>
                                    Review all the information provided for setting up the account. If any change is required, go to that 
                                    section directly and update the value and come back to review section. Click submit after reviewing all 
                                    the information.
                                </p>
                                <br />
                                <img className='help-images' src={AccountSetup} width="80%" height="80%" />
                            </p>
                        </p>
                    </div>
                    <div className="scroll" id="configs">
                        <h3 style={{ color: '#0097E4' }}>Key Configurations</h3>
                        <p>
                            This section provides different configurations for the QuickFabric platform. This contains two sections:
                            <p className='subsection'>
                                <br />
                                <h4>1. Account Configs</h4>
                                <p className='subsubsection'>
                                    Lets user manage account-level configurations. Select an account to see the existing configurations for that account. 
                                    Some of the values are encrypted, click on the Decrypt button on the right to see the original value.
                                    <br />
                                    <img className='help-images' src={AccountConfig} width="80%" height="80%" />
                                </p>
                                <h4>2. Application Configs</h4>
                                <p className='subsubsection'>
                                    Lets user manage global configurations.
                                    <br />
                                    <img className='help-images' src={AppConfig} width="80%" height="80%" />
                                </p>
                            </p>
                        </p>
                    </div>
                    <div className="scroll" id="features">
                        <h2 style={{ color: '#0097E4' }}>Features</h2>
                    </div>
                    <div className="scroll" id="observability">
                      <h3 style={{ color: '#0097E4' }}>EMR Observability</h3>
                      <h4>Active Clusters Graph</h4>
                      <p>
                          This page contains a graphical view of active clusters divided by AWS account and business segment.
                          The user will only see clusters for the AWS accounts and segments for which they have read access
                          (see <Link smooth to="/help#access">here</Link> for more on access control in QuickFabric).
                          
                          The end nodes of the graph each represent an active cluster and are color-coded according to 
                          its state. The possible states of a cluster are:
                          
                          <ul>
                              <li>
                                <strong style={{ color: '#4bfa54'}}>Healthy:</strong> The cluster is up and ready to accept new jobs
                              </li>
                              <li>
                                <strong style={{ color: '#e4ed32'}}>Running:</strong> The cluster currently has jobs running on it.
                                 The number of jobs will be displayed in the node after the cluster name.
                              </li>
                              <li>
                                <strong style={{ color: '#ed3232'}}>Unhealthy:</strong> The cluster's resource manager is not
                                responding, indicating some kind of problem on the cluster.
                              </li>
                          </ul>
                      </p>
                      <p>
                          The graph can be filtered based on cluster type and segment using the checkboxes on the right hand side.
                      </p>
                      <br />
                      <img className='help-images' src={graph} width="80%" height="80%" />
                      <p>
                          Using the "Cost" switch on the upper-left hand corner of the graph, the user can see the current
                          cost of the cluster for the current month to date.
                      </p>
                      <h4>Drill Down into a Cluster</h4>
                      <p>
                        Clicking on the node opens the detailed information about that particular EMR cluster.  
                        In this panel, the user can see the metrics history of the cluster, and can filter the
                        amount of time they want to look at using the radio buttons at the top of the window.
                        Predefined options to view the metrics history include over the last 10 minutes, last
                        hour, last day (24 hours), week, and month.
                      </p>
                      <h5>Cluster Details</h5>
                      <p>
                        In the first tab, the user can see basic information about the cluster. This includes
                        the business owner, which is defined as the owner of the business segment that the
                        cluster belongs to, its ID, the resource manager URL, and the number of active nodes.
                      </p>
                      <p>
                        <br />
                        <img className='help-images' src={clusterDetails} width="80%" height="80%" />
                      </p>
                      <h5>Job Stats</h5>
                      <p>
                        The Job Stats page shows the history of jobs (applications) that have run on the selected
                        cluster. For all time filters greater than 10 minutes, the user can see graphs of the average
                        number of jobs running, pending, succeeded, and failed over time. These graphs will show 
                        always show hourly data, with the exception of the "last hour" filter which will show data
                        in 10 minute increments. For jobs running and pending, the numbers displayed are averages 
                        based on snapshots of the cluster taken every 10 minutes. For jobs succeeded/failed, the
                        numbers are the total number of jobs that failed/succeeded in each hour.
                      </p>
                      <p>
                        If the time filter is set to a duration of greater than 24 hours, i.e. day, week, month, or a custom
                        range greater than 24 hours, then the numbers shown are the average across a given hour of the
                        day. For example, if the time range filter is set to the last week, and the number of apps succeeded
                        at index 0 in the graph is 5, then this means that 5 is the average number of jobs that succeeded between
                        12:00 AM and 1:00 AM on each day of the week for the last week. If the time range filter is set to the last 
                        week, and the number of apps succeeded at index 20 in the graph is 6, then this means that 6 is the average 
                        number of jobs that were running at any given time between 8:00 PM and 9:00 PM on each day of the week 
                        for the last week.
                      </p>
                      <p>
                        At the bottom of the tab, there will always be metrics summaries for each of jobs succeeded, failed,
                        pending, and running. These numbers are aggregates across the entire time period selected (sums for 
                        jobs succeeded/failed, averages for running/pending). For the last 10 minutes filter, the "Average Jobs 
                        Running" row has a link to show details of the jobs currently running on the cluster, including the 
                        application ID, type of the job (Spark, Tez, MapReduce, etc.), elapsed time in minutes, progress as a percentage,
                        and the user executing the job. Note that the number shown is the number of jobs running as of the last
                        snapshot which gets refreshed every 10 minutes, so if any jobs completed or began in the intervening time 
                        than the number of jobs shown may not match the number in the table.
                      </p>
                      <h5>System Stats</h5>
                      <p>
                        Similar to Job Stats, for time ranges greater than 1 hour, the user will see graphs of hourly memory and 
                        CPU cores usage as a percentage. These are also averages of the snapshots of the cluster 
                        which are taken every 10 minutes, and for periods greater than 24 hours the graph will display 24 hours
                        where each index corresponds to an hour of the day, and the number shown is the average over that hour
                        for each day within the specified time period. At the bottom of this tab will show the average memory and
                        CPU cores usage over the entire time period.
                      </p> 
                      <p>
                        <br/>
                        <img className='help-images' src={clusterDetails1} width="80%" height="80%" />
                      </p>
                      <br />                  
                      <h5>Cost</h5>
                      <p>
                        Cost section shows the cost incurred by the cluster. For time intervals filter Weekly, Monthly and Custom Date, 
                        there is also a chart showing the trend of the cost incurred by this cluster over the time interval provided.
                        <br />
                        <img className='help-images' src={ClusterCost} width="80%" height="80%" />
                      </p>
                      <h5>Expert Advice</h5>
                      <p>
                        This tab gives plain English advice on how to improve how you use your EMR cluster to optimize performance
                        while minimizing cost. Currently, QuickFabric supports two kinds of advice. In job scheduling advice, the
                        user can see recommendations on how to improve load-balancing on the cluster by moving jobs from hours/days 
                        where the most jobs are running to hours/days where the least jobs are running. QuickFabric also integrates
                        with Dr. Elephant from LinkedIn to provide job tuning advice. At the top level, QuickFabric provides a summary
                        of how many jobs need tuning, categorized into severity levels of "Critical", "Severe", and "Moderate" 
                        as defined by Dr. Elephant. This can then be expanded to show the individual applications that need tuning,
                        filterable by severity level. Each job can then be expanded further to show heuristics that explain why the
                        job is in critical state. A heuristic is essentially an apsect of the performance of the job. This subtable 
                        shows the name of the heuristic, advice on what to do to improve that particular heurstic, and the severity
                        of that particular heuristic. For more on Dr. Elephant and heuristics, see 
                        <a href='https://github.com/linkedin/dr-elephant/wiki/Metrics-and-Heuristics#heuristics'>here</a>.
                      </p>
                      <p>
                          <br />
                          <img className='help-images' src={expertAdvice} width="80%" height="80%" />
                      </p>
                    </div>

                    <div className="scroll" id="orchestration">
                    <h3 style={{ color: '#0097E4' }}>EMR Orchestration</h3>
                        <h4>Workflow Management</h4>
                        <p>
                        QuickFabric supports multiple actions for EMR clusters, such as creating a new cluster, adding jobs to an existing cluster, 
                        terminating a cluster etc. These actions once initiated go through multiple steps before completion, which takes considerable 
                        amount of time. These actions can either successfully pass or fail upon completion, without showing where and why it failed. 
                        To provide better experience to the user, we created workflow visualizations for certain actions on QuickFabric. A workflow 
                        breaks down an action into multiple steps and shows the current state of the action performed. A steps once completed successfully 
                        is visualized with a green check mark. If a step is in progress a blinking yellow dot is shown. If the step failed, there will 
                        be a red exclamation mark. This feature keeps the user better informed about the actions performed by them.
                        <br />
                        <img className='help-images' src={workflow} width="80%" height="80%" />
                        <br />
                        The workflow visualization provides the user the option to view more details about any particular step in the workflow. 
                        Below shown is the workflow visualization for creating a cluster.
                        <br />
                        <img className='help-images' src={createClusterWf} width="80%" height="80%" />
                        </p>
                        <h4>Create Cluster</h4>
                        <p>
                            With a myriad of options and features for EMR clusters on AWS, a cluster creation can feel overwhelming. Keeping this in mind 
                            QuickFabric aims to make the cluster creation simpler with only required information to fill in to create a new cluster. The form 
                            is user-friendly and handles error validations. The entire form is divided into following sub-sections:
                            <br />
                            <br />
                            <p className='subsection'>
                                <h5>1. Cluster Details:</h5> Provide basic information about the cluster such as: 
                                <p className='subsubsection'>
                                    <br />
                                    <b>Cluster Type:</b> 
                                    <br />
                                    <ul>
                                        <li>
                                            <strong>Exploratory</strong> clusters are meant for ad-hoc jobs run by analysts.
                                        </li>
                                        <li>
                                            <strong>Scheduled</strong> clusters are for jobs that are run regularly and maintained by
                                            a scheduling framswork such as Tidal.
                                        </li>
                                        <li>
                                            <strong>Transient</strong> clusters are clusters which get provisioned to execute one set
                                            of jobs immediately upon creation. The job(s) to be executed will be passed as cluster steps.
                                            Upon completion of these jobs, the cluster is immedately terminated.
                                        </li>
                                    </ul>
                                    <b>Cluster Sub Type: </b>
                                    Currently QuickFabric only supports non-kerberized clusters.
                                    <br />
                                    <br />
                                    <b>Cluster Segment: </b>
                                    Segment defines the group where the cluster will be used.
                                    <br />
                                    <br />
                                    <b>Account ID: </b> AWS Account where the cluster should be created.
                                    <br />
                                    <br />
                                    <b>Is Production Cluster: </b> Whether this cluster will be used in production environment or only for testing.
                                    <br />
                                    <br />
                                    <b>Cluster Name: </b>Name of the cluster. Should be unique, otherwise the cluster creation will fail. The final cluster name 
                                    used for the creation process is created by appending the cluster type, cluster segment and the name provided in this field.
                                    E.g. exploratory-sales-abctest.
                                </p>
                                <h5>2. Hardware:</h5> Provide hardware information for the cluster.
                                <p className='subsubsection'>
                                    <br />
                                    <b>Master Node Instance Type: </b> Select an EC2 instance type for the Master Node. Default is m5.xlarge.
                                    <br />
                                    <br />
                                    <b>Core Instance Count: </b> Provide the number of core node instances. Default value is 0.
                                    <br />
                                    <br />
                                    <b>Core Node Instance Type: </b> Select an EC2 instance type for the Core Nodes. This field is displayed only if the Core Instance Count is greater than 0.
                                    <br />
                                    <br />
                                    <b>Task Instance Count: </b> Provide the number of task node instances. Default is 0.
                                    <br />
                                    <br />
                                    <b>Task Node Instance Type: </b> Select an EC2 instance type for the Task Nodes. This field is displayed only if the Task Instance Count is greater than 0.
                                    <br />
                                    <br />
                                    <b>Attach Auto-Scaling:</b> Toggle button to attach Auto-Scaling to EMR. This field is displayed only if at least one of either Core Instance Count or Task Instance Count or both are greater than 0.
                                    <br />
                                    <br />
                                    <b>Instance Group: </b> Select the instance to which the auto-scaling will be attached to. The drop down contains: CORE, if Core Instance Count is greater than 0, and TASK, if Task Instance Count is greater than 0.
                                    This field is displayed only is Auto-Scaling is On.
                                    <br />
                                    <br />
                                    <b>Instance Min Count:</b> Provide minimum instance count for auto-scaling. This field is displayed only is Auto-Scaling is On.
                                    <br />
                                    <br />
                                    <b>Instance Max Count:</b> Provide maximum instance count for auto-scaling. This field is displayed only is Auto-Scaling is On.
                                </p>
                                <h5>3. Config:</h5>
                                <p className='subsubsection'>
                                    <b>Master Node EBS Volumne Size: </b> Select an EBS Volume Size for Master Node. Values are in Gigabytes. Default is 10GB.
                                    <br />
                                    <br />
                                    <b>Code Node EBS Volume Size: </b> Select an EBS Volume Size for Core Node. Values are in Gigabytes. Default is 10GB.
                                    <br />
                                    <br />
                                    <b>Task Node EBS Volume Size: </b> Select an EBS Volume Size for Task Node. Values are in Gigabytes. Default is 10GB.
                                    <br />
                                    <br />
                                    <b>AMI ID:</b> Provide an EC2 AMI ID. If not, default AWS AMI ID will be used.
                                    <br />
                                    <br />
                                    <b>Terminate Cluster After Completion: </b> Whether to terminate cluster after creation and completing all associated job executions.
                                    <br />
                                    <br />
                                    <b>Auto-Pilot for AMI Rotation: </b> Whether to rotate the cluster automatically after the specified number of days.
                                    <br />
                                    <br />
                                    <b>AMI Rotation Custom SLA</b> Provide the number of days after which auto-rotate AMI. Default is 30 days.
                                    <br />
                                    <br />
                                    <b>Headless Users: </b> Provide headless users to be associated with the cluster.
                                </p>
                                <h5>4. Add Bootstrap Actions: </h5> This section lets the user add bootstrap actions to be run on the cluster. By default this is empty. 
                                By clicking on the ‘+’ icon the user can provide the bootstrap action details. Multiple scripts can be added and any script can be deleted as well.
                                <br />
                                <br />
                                <h5>5. Add Steps: </h5>This section allows the user to add jobs to be run on the cluster. By default this is empty. 
                                By clicking on the ‘+’ icon the user can provide the job details. Multiple jobs can be added and any job can be deleted as well.
                                <br />
                                <br />
                                <h5>6. Review:</h5>
                                This is the summary of all the information entered by the user for creating a cluster. If the user wants to change my field they 
                                can do that by going to that particular section directly and updating the field. Then coming back to review section, it will show the updated data.
                            </p>
                        </p>
                        <p>
                        <br />
                        <img className='help-images' src={createCluster} width="80%" height="80%" />
                        </p>
                        <h4>Terminate Cluster</h4>
                        <p>
                            To terminate a cluster, click on the terminate icon and a popup will open to enter the cluster name. This makes sure that the user is 
                            deleting the right cluster. On submit there is another popup confirming the deletion of the cluster and whether to continue. These 
                            alerts make sure that no cluster is deleted by accident. Once the deletion is initiated successfully, a success toaster will appear 
                            and the page will refresh with the latest data. 
                            <br />
                            There is also a toggle for Auto-terminate clusters. By default the value is off. User has the option to auto-terminate the cluster once all the associated jobs complete execution.
                            If there are no jobs for the cluster, that cluster will be terminated.
                            <br />
                        <img className='help-images' src={TerminateCluster} width="80%" height="80%" />
                        </p>
                        <h4>Add Step</h4>
                        <p>
                            To add new jobs to an existing cluster, click on the Add Step button and click on the icon in the last column of the table for a particular 
                            cluster. This will open a popup window to enter information about new jobs to be added to the cluster. On submit, a confirmation box will 
                            show up to reassure the action to be performed.
                            <br />
                            <img className='help-images' src={AddSteps} width="80%" height="80%" />
                        </p>
                        <h4>Rotate AMI</h4>
                        <p>
                            To rotate AMI for a cluster click on the Rotate AMI button in the action bar on the left. In the Actions column of the table there is a rotate icon, which on click opens 
                            a popup window, asking for cluster name, custom AMI ID (optional), whether this is a production cluster, and whether you would like to perform auto-rotation of AMI every month, if yes
                            provide a time window for this. On clicking Submit, a confirmation box opens to reassure the action to be performed.
                            <br />
                            <img className='help-images' src={RotateAMI} width="80%" height="80%" />
                            <br />
                            The Actions column also contains a toggle which shows whether auto-rotate AMI is on or off. Clicking on the toggle you can update the selection.
                            By default the SLA for auto-rotate AMI is 30 days. This value can be changed from the text field provided in the Auto Pilow Window & Days column in the table. 
                            If the Auto-Pilot toggle is on, this column also displays the time window for rotating AMI automatically. These values can be updated within the table.
                            <br />
                            <img className='help-images' src={AutoPilot} width="80%" height="80%" />
                        </p>
                        <h4>Test Suites</h4>
                        <p>
                            To run tests on the clusters, go to Test Suites section from the left hand side action bar, then click on the test icon in the Action column on the right hand side. This opens a popup which 
                            contains the list of tests that can be run on the given cluster based on the segment it belongs to. The two tabs show the tests currently running, and the history of all the tests run on this cluster. 
                            The tests' status is shown in the workflow-style visualization, with blinking yellow dot representing running state, green check as completed and red exclamation as failed.
                            Clicking on Run Tests button runs all the tests on the cluster. If the tests are already running and if the user clicks on the Run Tests button, a popup will open asking whether restart the tests 
                            execution or keep the currently running tests.
                        </p>
                        <h4>Clone Cluster</h4>
                        <p> 
                            This action provides the user the ability to create a new cluster with properties of an existing cluster. Clone Cluster table 
                            shows the list of all the clusters (including terminated), which the user can clone. On clicking the icon for the cluster to be 
                            cloned, the create cluster form will open with all the fields filled in  with the values of the old cluster (to be cloned). Cluster 
                            name is not copied to the form because new cluster cannot be created with the same name, hence to avoid that mistake the name field is left empty.
                            <br />
                            <img className='help-images' src={CloneCluster} width="80%" height="80%" />
                        </p>
                        <h4>Flip to Production</h4>
                        <p>
                            Users sometimes clusters for testing some features. If later the user wishes to change this cluster to production cluster, this functionality 
                            helps user achieve that. Click on the Flip to Production button on the action bar on the left side to open the table with flip icon for each 
                            cluster row. Clicking on the flip for a particular cluster opens a popup, asking for the cluster name for confirmation and the full DNS name. 
                            On clicking confirm, another confirmation box opens to reassure this action. On confirming on this popup the action is performed.
                            <br />
                            <img className='help-images' src={DNSFlip} width="80%" height="80%" />
                        </p>
                    </div>
                    <div className="scroll" id="cost">
                      <h3 style={{ color: '#0097E4' }}>EMR Cost</h3>
                      <p>
                          QuickFabric helps you to keep track of how your clusters are performing and how much cost in incurred from maintaining these clusters. 
                          EMR Cost section provides cost details about different clusters. The table contains basic information about the clusters and last six months consists
                          associated with these clusters. The table also shows a graph created from the last six month values to see the trend in the cost.
                          <img className='help-images' src={EMRCost} width="80%" height="80%" />
                      </p>
                    </div>
                    <div className="scroll" id="access">
                        <h3 style={{ color: '#0097E4' }}>Access Control</h3>
                        <p>
                        QuickFabric allows for users to manage and monitor EMR clusters across various accounts and 
                        business segments, but each user should only have the ability to do certain actions for clusters 
                        belonging to particular AWS accounts and business segments. QuickFabric has specially defined roles
                        that combine with a particular AWS account and business segment to define a permission.
                        
                        QuickFabric has the following user roles:
                          <ul>
                            <li>Read</li>
                            <li>Create Cluster</li>
                            <li>Terminate Cluster</li>
                            <li>Add Step</li>
                            <li>Rotate AMI</li>
                            <li>Flip DNS</li>
                            <li>Clone Cluster</li>
                            <li>Admin</li>
                          </ul>
                        
                        For example, if your company has two accounts "1234" and "4321", and two segments "sales" and "marketing",
                        User 1 can have read access to all combinations of accounts and segments but be unable to perform any actions.
                        User 2 can have admin priveleges to both segments on account "1234" but no permision to anything on account
                        "4321", and User 3 can have Read, Create Cluster, Terminate Cluster, and Rotate AMI permissions for "sales" in
                        both accounts but no permissions for "marketing" in either cluster, and so on.
                        </p>
                        <p>
                        All the data and accessible to the user after logging into the platform is filtered based on their permissions.
                        Access control is applied to the cluster information, the actions that the user can perform on the clusters, 
                        the type of cluster the user can create. User can view the permissions he has in the User Profile section. 
                        </p>
                    </div>
                    <div className="scroll" id="admin">
                    <h3 style={{ color: '#0097E4' }}>Administration</h3>
                        <p>
                        This page is available to only super admin users, to handle accounts and users on QuickFabric. 
                        </p>
                        <img className='help-images' src={admin} width="80%" height="80%" />
                        <h4>User Management</h4>
                        <p>
                        This provides the capability to manage users and the permissions and access they have to perform different actions on the clusters. 
                        User Management has three following tabs:
                        <br />
                        <br />
                        <p className='subsection'>
                            <h5>1. Add Roles</h5>
                            Here the super admin can add new permissions and access to an existing user, or if the user does not exist, it creates a new user 
                            with the mentioned permissions. The Add Roles form contains the following fields:
                            <p className='subsubsection'>
                                <br />
                                <b>User Email ID: </b>
                                The email ID of the user for whom the roles need to be changed. The field checks whether the entered email ID 
                                is a valid email format. If not an error is shown  “Please enter a valid e-mail”. If valid, a check is performed to see if the user 
                                already exists and has an account on QuickFabric. If yes, then the existing roles assigned to the user is displayed in the tabular 
                                format below the form. This helps the super admin to review the roles to be added to that user. If this is a new user, a message is 
                                displayed below this field “E-mail ID does not exist. Proceed to create a new User”. This means on submitting this form, a new user 
                                with the specified roles will be created. This user will have a random password generated automatically by the system to login to 
                                QuickFabric platform. The super admin can reset their password from the third tab mentioned below in detail.
                                <br />
                                <br />
                                <b>First Name:</b> Provide the first name of the user.
                                <br />
                                <br />
                                <b>Last Name:</b> Provide the last name of the user.
                                <br />
                                <br />
                                <b>Service:</b> This is a multi-select field. From the dropdown select the services for which the user can perform actions on the cluster. 
                                <br />
                                <br />
                                <b>Segments:</b> This is a multi-select field. From the dropdown select the segments for which the user can perform action on the cluster.
                                <br />
                                <br />
                                <b>Actions:</b> This is a multi-select field. From the dropdown select actions the user can perform on the clusters.
                                <br />
                                <br />
                                <b>AWS Accounts:</b> This is a multi-select field. From the dropdown select the AWS accounts. The user can perform actions to the clusters 
                                belonging to these AWS accounts only.
                                <br />
                                <br />
                                <b>Add Roles button:</b> On submitting, a popup opens for confirmation. On continuing the form is submitted, either providing the roles to 
                                the user or creating a new user with the specified roles.
                            </p>
                            <h5>2. Remove Roles</h5>
                            The super admin can also remove any permissions and access for an existing user. Remove Roles contains the same fields as the Add Roles. 
                            On submitting, a popup opens for confirmation. On continuing roles are removed for the user. If the user doesn’t exist then the message is 
                            shown when the user email ID is entered. Hence the super admin will be informed and no action is required to be taken.
                            <br />
                            <br />
                            <h5>3. Reset Password</h5>
                            This allows the super admin to change the password for another existing user. The form contains the following fields:
                            <p className='subsubsection'>
                                <br />
                                <b>User Email ID:</b> Email ID of the user for whom the password need to be reset.
                                <br />
                                <br />
                                <b>New Password:</b> New password for the user account.
                                <br />
                                <br />
                                This feature is useful for resetting password for newly created accounts, as the system generates random password for those new users.
                            </p>
                        </p>
                        </p>
                    </div>
                    <div className="scroll" id="profile">
                    <h3 style={{ color: '#0097E4' }}>User Profile</h3>
                        <p>
                        This section contains information about the user. The basic information section contains full name, Email ID, whether the user is a super admin and 
                        when the account was created. Below are the tabs present on the page:
                        <br />
                        <img className='help-images' src={profile} width="80%" height="80%" />
                        <p className='subsection'>
                            <h5>1. My Access: </h5>
                            This section shows what the access the user has to perform actions on clusters. The table shows the actions that can be performed on clusters belonging to service and segment in a specific AWS account.
                            <br />
                            <br />
                            <h5>2. My Clusters: </h5>
                            This section contains a table showing the clusters creating by this user and all the information about those clusters.
                        </p>
                        </p>
                    </div>
                    <div className="scroll" id="subscription">
                        <h4 style={{ color: '#0097E4' }}>Report Subscriptions</h4>
                        <p>
                          Sometimes as convenient as QuickFabric's application is for monitoring your EMRs, you just don't want to leave the
                          comfort of your preferred email client. To address this, QuickFabric offers the ability to subscribe to daily email
                          reports containing only the kinds of information that are most relevant to you. Currently, QuickFabric currently 
                          supports a metrics report and an AMI rotation report.
                        </p>
                        <p>
                          In the "My Subscriptions" tab of the User Profile page, the user will see several toggles to subscribe or unsubscribe
                          to the various reports. These toggles come prepopulated with the user's current subscriptions.
                          Each time a subscription gets changed by updating the toggle, the user's updated subscriptions
                          will be immediately saved, meaning that the user's most up-to-date subscriptions will be exactly the same as what they
                          see on the page. 
                        </p>
                        <p>
                          Each day at 9 AM, the daily report will be sent out. Each user will only receive reports to clusters for which they
                          have read access (see <Link smooth to="/help#admin">here</Link> for more on QuickFabric access control). The email
                          is divided into sections based on the reports the user has subscribed to. The user will also by default receieve all
                          reports for clusters which they have created. QuickFabric assumes that since that user has created the cluster they
                          are the cluster owner and have the responsibility to have up-to-date reporting on aspects of it such as metrics and
                          cost.
                        </p>
                        <p>
                          In addition to per user subscriptions, QuickFabric supports email reporting for administrators. These come in two
                          flavors. The first is daily, weekly, and monthly reports that get sent to a mailing list containing the addresses of
                          administrators. The reports contain all reports about every cluster. The second flavor is a daily report that is
                          sent to the owners of individual business segments. All kinds of reports will be included in the email, but only
                          for the clusters belonging to the business segment that they own.
                        </p>
                        <p>
                          To see how to enable and configure reporting emails, see <Link smooth to="/help#configurations">here</Link>.
                        </p>
                    </div>
                    <div className="scroll" id="notification">
                      <h2 style={{ color: '#0097E4' }}>Email Notification</h2>
                      <p>
                          You may want to be informed in real time about all the important events happening with the EMRs on your account.
                          To this end, QuickFabric supports the ability to receieve email notifications when someone performs an action
                          such as creating a cluster, terminating a cluster, rotating AMI, or flipping a cluster to production. The body
                          of the email will contain details of the clusters affected, such as the EMR's name and ID, the account it belongs
                          to, and its current status. The user who performed the action will receieve the email, as well as email addresses
                          specified by the owner of the account. See more on how to set up email notifications  
                          <Link smooth to="/help#configurations">here</Link>.
                      </p>
                    </div>
                    <div className="scroll" id="configurations">
                      <h3 style={{ color: '#0097E4' }}>Configurations</h3>
                      <p>
                        QuickFabric is personalized to each company's needs using configurations. There are currently two levels of configurations
                        in QuickFabric: application level and account level. Some of these need to be set up in order for the application to
                        function properly, and others will be automatically filled with default values that can be updated later to suit your 
                        needs. Application level configurations define how the entire system behaves. They control behavior that will affect all users,
                        independent of the clusters and accounts to which they have access. At the next level is account level configurations,
                        which control the behavior of different functionality only for a specific AWS account.
                        <br />
                        <br />
                        Below is a list of all adjustable configurations in QuickFabric, organized by function.
                      </p>
                      <p>
                        
                      </p>
                      <h4>Single Sign-On</h4>
                      <p>
                        QuickFabric supports SSO and can be enabled and set up using the configuration management page. To set it up,
                        adjust the below configurations. All are at the application level, so will impact all users and can only be 
                        adjusted by a Super Admin.
                      </p>
                      <p className="subsection">
                          <b>sso_enabled:</b> Enables or disable single sign-on. If this is set to true, then all other 
                          configurations must be updated so that it can work.
                          <br />
                          <br />
                          <b>sso_url:</b> The url that invokes the Single Sign-On service, i.e. will either use the existing
                          credentials if the user has already logged in to this or another SSO-enabled application, or will prompt the
                          authentication process.
                          <br />
                          <br />
                          <b>sso_redirect_url: </b>The URL to which the SSO service will redirect the user after successful
                          authentication.
                          <br />
                          <br />
                          <b>sso_email_key: </b>The email key for single sign-on.
                          <br />
                          <br />
                          <b>sso_qbn_authid: </b>The authentication ID for single sign-on.
                          <br />
                          <br />
                          <b>sso_qbn_ptc_authid:</b> The PTC authentication ID for single sign-on.
                          <br />
                          <br />
                          <b>sso_qbn_tkt: </b>Ticket for single sign-on.
                          <br />
                          <br />
                          <b>sso_qbn_ptc_tkt: </b>PTC Ticket for single sign-on.
                          <br />
                          <br />
                          <b>sso_shared_key:</b> Key used to decrypt single sign-on data.
                      </p>
                      <h4>Request Ticket Validation</h4>
                      <p>
                        QuickFabric supports the ability for users to require an approved ticket from either Jira or ServiceNow to perform 
                        cluster actions. This ensures that all actions on clusters have explicit approval, are completed by the person who
                        has received the approval for the action, and are well documented, as the ticket ID is stored. To configure this
                        functionality, first modify the following application level configurations: 
                      </p>
                      <p className="subsection">
                          <b>jira_enabled_global: </b>Set this to true to allow Jira ticket validation. This will usually be set
                          to true if Jira is ever used, because it is still up to individual AWS account owners to decide whether they want
                          to require ticket validation for their respective accounts. This flag can be thought of a kill-switch in case there
                          is a problem with Jira, e.g. maybe the Jira server is down and is blocking EMR management. In that case, you may want
                          to temporarily Jira ticket validation on a global level to allow cluster management to continue in the meatime.
                          <br />
                          <br />
                          <b>servicenow_enabled_global: </b>Set this to true to allow ServiceNow ticket validation. This will usually be set
                          to true if ServiceNow is ever used, because it is still up to individual AWS account owners to decide whether they want
                          to require ticket validation for their respective accounts. This flag can be thought of a kill-switch in case there
                          is a problem with ServiceNow, e.g. maybe the ServiceNow server is down and is blocking EMR management. In that case, you may want
                          to temporarily Jira ticket validation on a global level to allow cluster management to continue in the meatime.
                      </p>
                      <p>
                        Individual AWS accounts can have their own distinct settings regarding if and how they want request ticket validation 
                        enabled for actions on clusters belonging to their accounts.
                        <br />
                        For Jira, all of the following must be set up:
                      </p>
                      <p className="subsection">
                          <b>jira_enabled_account: </b>Requires Jira ticket validation for the given account, assuming 
                          <strong> jira_enabled_global</strong> is also enabled.
                          <br />
                          <br />
                          <b>jira_url:</b> In order to perform the validation, QuickFabric will need to access the Jira API. This 
                          configuration's value is the url that provides access to the Jira API, including the full path minus the issue number.
                          An example value would be "jira.yourcompany.com/rest/api/latest/issue/".
                          <br />
                          <br />
                          <b>jira_user: </b>In order to perform the validation, QuickFabric will need to access the Jira API. This 
                          configuration's value is the user which has read access to your company's Jira server.
                          <br />
                          <br />
                          <b>jira_password: </b>In order to perform the validation, QuickFabric will need to access the Jira API. This 
                          configuration's value is the password for the user which has read access to your company's Jira server. This value will
                          necessarily be encrypted in QuickFabric's backend.
                          <br />
                          <br />
                          <b>jira_projects: </b>Jira tickets for cluster actions under a particular acocunt will only be valid from certain
                          projects. Enter these here, comma separated. Sample value would be "DATAPROJ,JIRAPROJ,DATAPROJ2".
                      </p>
                      <p>
                        For ServiceNow:
                      </p>
                      <p className="subsection">
                          <b>snow_enabled_account: </b>Requires Jira ticket validation for the given account, assuming 
                          <strong> snow_enabled_global</strong> is also enabled.
                          <br />
                          <br />
                          <b>snow_url: </b>In order to perform the validation, QuickFabric will need to access the ServiceNow API. This 
                          configuration's value is the url that provides access to the ServiceNow API, including the full path to the incident task
                          table. An example value would be "https://intuitdev01.service-now.com/api/now/table/u_incident_task".
                          <br />
                          <br />
                          <b>snow_user: </b>In order to perform the validation, QuickFabric will need to access the ServiceNow API. This 
                          configuration's value is the user which has read access to your company's Jira server.
                          <br />
                          <br />
                          <b>snow_password: </b>In order to perform the validation, QuickFabric will need to access the ServiceNow API. This 
                          configuration's value is the password for the user which has read access to your company's ServiceNow. This value will
                          necessarily be encrypted in QuickFabric's backend.
                      </p>
                      <h4>Cluster Auto Termination</h4>
                      <p>
                        Sometimes you want to create EMR clusters just as a test for various reasons. However, you may not want these clusters to
                        persists for too long to avoid racking up unwanted costs for the resources they are allocated. QuickFabric will optionally
                        automatically terminate clusters with the word "test" appearing in the name after a certain period of time. To set this up,
                        update the following configurations for each individual AWS account:
                      </p>
                      <p className="subsection">
                          <b>test_cluster_auto_termination:</b> Set to true if test clusters should be automatically terminated.
                          <br />
                          <br />
                          <b>test_cluster_ttl: </b>he amount of time in hours that test clusters should persist before being automatically
                          terminated.
                      </p>
                      <h4>Cluster Action Notifications</h4>
                      <p>
                        Update these configurations to manage email notifications for cluster actions. 
                        See <Link smooth to="/help#notification">here</Link> for more details on this feature.
                      </p>
                      <p className="subsection">
                          <b>add_step_notifications: </b>Application level configuration for whether adding a step to a cluster results
                          in an email notification.
                          <br />
                          <br />
                          <b>create_cluster_notifications: </b>Application level configuration for whether creating a cluster results
                          in an email notification.
                          <br />
                          <br />
                          <b>dns_flip_notifications: </b>Application level configuration for whether flipping a cluster into production results
                          in an email notification.
                          <br />
                          <br />
                          <b>rotate_ami_notifications: </b>Application level configuration for whether performing a manual AMI rotation of a 
                          cluster results in an email notification.
                          <br />
                          <br />
                          <b>terminate_cluster_notifications: </b>Application level configuration for whether terminating a cluster results
                          in an email notification.
                          <br />
                          <br />
                          <b>notification_recipients: </b>This is a comma separated list of email recipients that should receive cluster
                          action email notifications, configured per account.
                          <br />
                          <br />
                          <b>from_email_address: </b>Application level configuration that specifies from whom cluster action notification
                          emails are sent.
                      </p>
                      <h4>Email Reports</h4>
                      <p>
                        Update these configuations to manage email reporting in QuickFabric. For more information on this feature,
                        see <Link smooth to="/help#subscriptions">here</Link>
                      </p>
                      <p className="subsection">
                          <strong>subscription_reports_scheduler:</strong> Global flag to turn on/off the scheduler that sends reports to users who
                          have subscribed to them.
                          <br />
                          <br />
                          <strong>segment_reports_scheduler:</strong> Global flag to turn on/off the scheduler that sends the daily 
                          reports regarding clusters belonging to specific business segments to the business owners of the 
                          respective segment.
                          <br />
                          <br />
                          <strong>daily_report_scheduler:</strong> Global flag to turn on/off the scheduler that sends the daily report 
                          of all clusters across all accounts to an email distribution list consisting of system admins.
                          <br />
                          <br />
                          <strong>weekly_report_scheduler:</strong> Global flag to turn on/off the scheduler that sends the weekly report 
                          of all clusters across all accounts to an email distribution list consisting of system admins.
                          <br />
                          <br />
                          <strong>monthly_report_scheduler:</strong> Global flag to turn on/off the scheduler that sends the monthly report 
                          of all clusters across all accounts to an email distribution list consisting of system admins.
                          <br />
                          <br />
                          <strong>report_recipients:</strong> Comma separated list that should contain the email addresses of administrators
                          who should see reports of all clusters across all accounts and business segments. 
                          <br />
                          <br />
                          <strong>from_email_address:</strong> Application level configuration that specifies from whom email reports
                          are sent.
                      </p>
                      <h4>Test Suites</h4>
                      <p>
                        To allow users to be assured that their EMR clusters are operating without issues, QuickFabric supports test suite 
                        execution on the cluster upon creeation. To enable these, update the following configurations:
                      </p>
                      <p className="subsection">
                          <strong>testsuites_enabled:</strong> For each individual AWS account, set this flag to allow test suites for clusters
                          that belong to it.
                          <br />
                          <br />
                          <strong>verify_number_of_bootstraps_scheduler:</strong> Global flag to turn on/off the scheduler that checks the
                          number of bootstraps. Note that since this is an application level configuration, and the test suite definition
                          needs to be enabled for the account during the account setup process in order to run anyway, this configuration
                          probably should be left turned on unless you want to explicitly prohibit ever running this test.
                          <br />
                          <br />
                          <strong>autoscaling_config_test_scheduler:</strong> Global flag to turn on/off the scheduler that
                          checks autoscaling. Note that since this is an application level configuration, and the test suite definition
                          needs to be enabled for the account during the account setup process in order to run anyway, this configuration
                          probably should be left turned on unless you want to explicitly prohibit ever running this test.
                      </p>
                      <h4>Database Cleanup</h4>
                      <p>
                        Metadata, metrics, test suites, and so on for clusters will persist in the QuickFabric database forever. 
                        This scheduler will make it so that clusters that have been terminated for over 6 months have all information
                        wiped from the database to preserve storage. Turn the <strong>rds_cleanup_scheduler</strong> configuration on
                        to enable this feature.
                      </p>
                      <h4>System Schedulers</h4>
                      <p>
                        The following schedulers, while technically configurable, enable some critical component of how QuickFabric operates.
                        These have default values set, and you should only change them if you know what you're doing. It is most likely that
                        you will never need or want to update these configurations.
                      </p>
                      <p className="subsection">
                          <strong>add_steps_to_clusters_scheduler:</strong> This scheduler executes pending steps on EMR clusters.
                          <br />
                          <br />
                          <strong>auto_ami_rotate_scheduler:</strong> This scheduler enables the autopilot feature for AMI rotation every 
                          preset period of time. Note that this configuration is global, and turning it off will prevent automatic AMI
                          rotation. Since this needs to be enabled on a per cluster basis to begin with, only turn this off if you want to
                          completely prohibit automatic AMI rotation across the application.
                          <br />
                          <br />
                          <strong>check_cluster_status_with_termination_initiated_scheduler:</strong>
                          Change the status of clusters to terminated if they were pending termination.
                          <br />
                          <br />
                          <strong>collect_cluster_metrics_scheduler:</strong> This scheduler runs every 10 minutes to collect the most
                          up-to-date information about cluster health and utilization from each cluster's resource manager.
                          <br />
                          <br />
                          <strong>terminate_completed_clusters_scheduler:</strong> This scheduler automatically terminates both clusters that
                          have been marked for termination due to a DNS flip or AMI rotation and test clusters that have exceeded their TTL.
                          Note that this is an application level configuration; for individual clusters that have been flipped or rotated,
                          automatic termination can be turned off and for each account, test cluster auto termination can be configured 
                          separately (see section above for details). Only turn off this scheduler if you want to prohibit this functionality
                          completely across the entire application.
                          <br />
                          <br />
                          <strong>validate_cluster_steps_scheduler:</strong>Scheduler that updates the status of steps once they are submitted to the cluster. 
                          <br />
                          <br />
                          <strong>validate_existing_clusters_scheduler:</strong>Scheduler that updates the status of exiting running cluster.
                          <br />
                          <br />
                          <strong>validate_new_clusters_scheduler:</strong>Scheduler that updates the status of cluster once the cluster is initiated.
                          <br />
                          <br />
                          <strong>collect_cluster_costs:</strong>Scheduler that collects cost of the cluster from AWS cost explorer.
                      </p>
                    </div>
                    <div className="scroll" id="videos">
                    <h2 style={{ color: '#0097E4' }}>Accolades</h2>
                    <iframe width="560" height="315" src="https://www.youtube.com/embed/TfgNzzaakNs?start=1324" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                    </div>
                    <div className="scroll" id="team">
                    <h2 style={{ color: '#0097E4' }}>Team</h2>
                        <table>
                            <tr>
                                <td>
                                    <div className='team-content'>
                                        <img src={Sandeep} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Sandeep Uttamchandani
                                            <br/>
                                            <i>Director Development</i>
                                        </p>
                                    </div>
                                </td>
                                <td>
                                    <div className='team-content'>
                                        <img src={Giri} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Giriraj Bagdi
                                            <br/>
                                            <i>Group Manager</i>
                                        </p>
                                    </div>
                                </td>
                                <td>
                                    <div className='team-content'>
                                        <img src={Khilawar} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Khilawar Verma
                                            <br/>
                                            <i>Staff Engineer</i>
                                        </p>
                                    </div>
                                </td>
                                <td>
                                    <div className='team-content'>
                                        <img src={Gaurav} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Gaurav Doon
                                            <br/>
                                            <i>Senior Software Engineer</i>
                                        </p>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div className='team-content'>
                                        <img src={Nisha} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Nisha Kunhikrishnan
                                            <br/>
                                            <i>Software Engineer 1</i>
                                        </p>
                                    </div>
                                </td>
                                <td>
                                    <div className='team-content'>
                                        <img src={Dan} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Dan Russotto
                                            <br/>
                                            <i>Software Engineer 1</i>
                                        </p>
                                    </div>
                                </td>
                                <td>
                                    <div className='team-content'>
                                        <img src={Varun} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Varun Sood
                                            <br/>
                                            <i>CW Software Engineer</i>
                                        </p>
                                    </div>
                                </td>
                                <td>
                                    <div className='team-content'>
                                        <img src={Kevin} width='200px' height='200px' />
                                        <p style={{ marginTop: '10px' }}>
                                            Kevin Tran
                                            <br/>
                                            <i>CW Software Engineer</i>
                                        </p>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                </div>
            </div>

        )
    }
}
