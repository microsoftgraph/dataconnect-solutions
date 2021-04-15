from setuptools import setup, find_packages

version = "0.1.7"

if __name__ == '__main__':
    setup(
        name="pygraph_utils",
        version=version,
        packages=find_packages(),
        python_requires='>=3.6',
        # include_package_data=True,
        # package_data={
        #     '': ['data/*.txt']
        # }
        url='https://github.com/microsoftgraph/dataconnect-solutions'
    )
