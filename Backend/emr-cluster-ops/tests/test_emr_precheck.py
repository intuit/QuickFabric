from src.scripts.emr_precheck import lambda_handler as emr_precheck
from pprint import pprint


def test_emr_prechcek_cluster_notpresent(lambda_context):
        cluster_precheck_request = {
                "api_request_id": "test_emr_precheck",
                "cluster_name": "nonkerb-testing-tes"
        }
        response = emr_precheck(cluster_precheck_request, lambda_context)
        assert 'status' in response
        print('Cluster status ::', response.get('status'))


def test_emr_prechcek_cluster_present(create_cluster, lambda_context):
        assert 'j-' in create_cluster.get('cluster_id')
        print('Cluster id', create_cluster.get('cluster_id'))

        cluster_name = create_cluster.get('cluster_name')

        cluster_precheck_request = {
                "api_request_id": "test_emr_precheck",
                "cluster_name": cluster_name
        }

        response = emr_precheck(cluster_precheck_request,lambda_context )
        assert 'status' in response
        print('Cluster status ::', response.get('status'))
