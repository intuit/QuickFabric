from src.scripts.emr_test_autoscaling import lambda_handler as list_asg
from pprint import pprint


def test_emr_list_asg(create_cluster, lambda_context):
    cluster_id = create_cluster.get('JobFlowId')
    assert 'j-' in cluster_id

    cluster_name = create_cluster.get('Name')

    list_asg_request = {
        "api_request_id": "test_list_asg",
        "cluster_name": cluster_name,
        "cluster_id": cluster_id,
        "instance_group": "CORE"
    }

    try:
        response = list_asg(list_asg_request, lambda_context)
    except Exception as error:
        print(error)
    else:
        assert 'status' in response
        print("Autoscaling details", response.get('status'))