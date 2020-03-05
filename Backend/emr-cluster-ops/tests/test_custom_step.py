from pprint import pprint
from src.scripts.emr_add_custom_steps import lambda_handler as emr_custom_steps


def test_emr_add_custom_step(create_cluster, lambda_context):
        cluster_id = create_cluster.get('cluster_id')
        cluster_name = create_cluster.get('cluster_name')
        cluster_type = create_cluster.get('cluster_type')

        assert 'j-' in cluster_id

        print(f"Cluster id {create_cluster.get('cluster_id')}")

        emr_add_custom_step_request = {
                "cluster_name": cluster_name,
                "account": create_cluster.get('account'),
                "step": "Setup Syslog",
                "cluster_id": cluster_id,
                "cluster_type": cluster_type,
                "steps": [{
                                "Name": "test-custom-steps",
                                "ActionOnFailure": "CONTINUE",
                                "HadoopJarStep": {
                                        "Jar": "script-runner.jar",
                                        "Args": ['test']
                        }
                }
                ]
        }

        response = emr_custom_steps(emr_add_custom_step_request, lambda_context)

        assert len(response.get('steps')) > 0

        print(f"steps {response.get('steps')}")
