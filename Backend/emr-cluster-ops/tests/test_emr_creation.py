import pytest
from pprint import pprint


def test_emr_create_cluster(create_cluster):
        print("\n")
        pprint(create_cluster, indent=4, compact=True)
        assert 'j-' in create_cluster.get('cluster_id')
