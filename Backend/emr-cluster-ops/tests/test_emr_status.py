from src.scripts.emr_status import lambda_handler as emr_status
from pprint import pprint


def test_emr_cluster_status_check(create_cluster, lambda_context):
        print("\n")
        pprint(create_cluster, indent=4, compact=True)
        assert 'j-' in create_cluster.get('cluster_id')

        cluster_id = create_cluster.get('cluster_id')

        cluster_status_request = {
                "api_request_id": "test_emr_status_check",
                "cluster_id": cluster_id
        }

        response = emr_status(cluster_status_request,lambda_context )
        assert 'status' in response
        print("\n")
        pprint(response)