from src.scripts.emr_terminate import lambda_handler as emr_terminate


def test_terminate_emr_cluster(create_cluster, lambda_context):
        cluster_id = create_cluster.get('cluster_id')
        cluster_name = create_cluster.get('cluster_name')
        cluster_type = create_cluster.get('cluster_type')

        assert 'j-' in cluster_id
        print(f"Cluster ID :: {cluster_id}")

        emr_terminate_request = {
                "api_request_id": "test_terminate_cluster",
                "cluster_name": cluster_name,
                "cluster_type": cluster_type,
                "cluster_id": cluster_id,
                "force": True
        }

        response = emr_terminate(emr_terminate_request, lambda_context)

        assert 'TERMINATION' in response.get('status')

        print(f"Cluster termination status :: {response.get('status')}")
