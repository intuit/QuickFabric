from src.scripts.emr_test_app import lambda_handler as emr_app


def test_emr_test_app(create_cluster, lambda_context):
    cluster_id = create_cluster.get('JobFlowId')
    assert 'j-' in cluster_id

    cluster_name = create_cluster.get('Name')

    test_app_request = {
        "api_request_id": "test_app_hive",
        "cluster_name": cluster_name,
        "cluster_id": cluster_id,
        "app": "hive"
    }

    try:
        response = emr_app(test_app_request, lambda_context)
    except Exception as error:
        print(error)
    else:
        assert 'step_id' in response
        print(f"Testing Hive app via step id {response.get('step_id')}")