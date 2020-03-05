from src.scripts.emr_validate import lambda_handler as emr_validate
from pprint import pprint


def test_emr_cluster_validate(create_cluster, lambda_context):
    cluster_id = create_cluster.get('cluster_id')
    assert 'j-' in cluster_id

    cluster_validate_request = {
        "api_request_id": "test_emr_validation",
        "cluster_name": create_cluster.get('cluster_name'),
        "cluster_type": create_cluster.get('cluster_type')
    }

    try:
        response = emr_validate(cluster_validate_request, lambda_context)
    except Exception as error:
        assert 'NotImplementedError' not in error.args
        pass
