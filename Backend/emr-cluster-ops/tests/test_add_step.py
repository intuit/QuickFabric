from pprint import pprint
from src.scripts.emr_add_steps import lambda_handler as emr_add_step


def test_emr_cluster_add_step(create_cluster, lambda_context):
        cluster_id = create_cluster.get('cluster_id')
        cluster_name = create_cluster.get('cluster_name')
        cluster_type = create_cluster.get('cluster_type')

        assert 'j-' in cluster_id

        print(f"Cluster id {create_cluster.get('cluster_id')}")

        emr_add_step_request = {
                "cluster_name": cluster_name,
                "account": create_cluster.get('account'),
                "step": "Setup Syslog",
                "cluster_id": cluster_id,
                "cluster_type": cluster_type}

        response = emr_add_step(emr_add_step_request, lambda_context)

        assert 'step_id' in response
        print(f"step id {response.get('step_id')}")


