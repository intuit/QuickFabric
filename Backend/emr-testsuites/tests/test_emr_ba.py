from src.scripts.emr_test_bootstrap import lambda_handler as emr_bas


def test_emr_bas(create_cluster, lambda_context):
    cluster_id = create_cluster.get('JobFlowId')
    assert 'j-' in cluster_id

    cluster_name = create_cluster.get('Name')

    bas_request = {
        "api_request_id": "test_bootstrap_actions",
        "cluster_id":  cluster_id,
        "cluster_name": cluster_name
    }
    try:
        response = emr_bas(bas_request, lambda_context)
    except Exception as error:
        print(error)
    else:
        assert 2 == response.get('bootstrap_count')
        print("bootstrap count", response.get('bootstrap_count') )
        print("bootstraps", response.get('bootstrap_names') )