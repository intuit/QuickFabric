from setuptools import setup

test_requirements = ['pytest','pytest-sugar',
                     'boto3', 'aws_lambda_context', 'coverage',
                     'pytest-cov', 'moto']


setup(
    setup_requires=['pyyaml', 'pytest-runner', 'pytest'],
    dependency_links=[
        'git+https://github.com/sandipnahak/moto.git@master#egg=moto'
    ],
    install_requires=['pytest'] + test_requirements,
    tests_require=['pytest'] + test_requirements
)