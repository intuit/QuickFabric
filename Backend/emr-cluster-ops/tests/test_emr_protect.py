from src.scripts.emr_protect import lambda_handler as emr_protect


def test_emr_cluster_protect(create_cluster, lambda_context):
        cluster_id = create_cluster.get('cluster_id')
        cluster_name = create_cluster.get('cluster_name')
        cluster_type = create_cluster.get('cluster_type')

        assert 'j-' in cluster_id
        print(f"Cluster ID :: {cluster_id}")

        emr_protect_request = {
                "api_request_id": "test_terminate_cluster",
                "cluster_name": cluster_name,
                "cluster_type": cluster_type,
                "cluster_id": cluster_id,
                "termination_protected": "status"
        }

        response = emr_protect(emr_protect_request, lambda_context)
        assert response.get('terminationProtected') in ['enabled', 'disabled']

        print(f"Cluster termination protection status :: {response.get('terminationProtected')}")
