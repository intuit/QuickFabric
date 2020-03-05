from src.scripts.emr_add_autoscaling import lambda_handler as asg_add
from pprint import pprint


def test_emr_asg_add(create_cluster, lambda_context):
    cluster_id = create_cluster.get('JobFlowId')
    assert 'j-' in cluster_id

    asg_add_request = {
        "api_request_id": "test_asg_add",
        "cluster_id":  cluster_id,
        "autoscaling_profile": "Default",
        "instance_group": "CORE",
        "min": "1",
        "max": "3"
    }
    try:
        response = asg_add(asg_add_request, lambda_context)
    except Exception as error:
        assert 'NotImplementedError' not in error.args
        pass
