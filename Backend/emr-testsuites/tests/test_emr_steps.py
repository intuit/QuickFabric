from src.scripts.emr_test_steps import lambda_handler as emr_steps


def test_emr_steps(create_cluster, lambda_context):
    cluster_id = create_cluster.get('JobFlowId')
    assert 'j-' in cluster_id

    cluster_name = create_cluster.get('Name')

    steps_request = {
        "api_request_id": "test_bootstrap_actions",
        "cluster_id":  cluster_id,
        "cluster_name": cluster_name
    }

    try:
        response = emr_steps(steps_request, lambda_context)
    except Exception as error:
        print(error)
    else:
        assert 2 == response.get('steps_count') and 'steps' in response
        print("Step count", response.get('steps_count') )
        print("Steps", response.get('steps'))