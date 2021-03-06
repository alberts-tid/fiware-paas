# PaaS Manager Acceptance Tests

Folder for acceptance tests of the Paas Manager.

## How to Run the Acceptance Tests

### Prerequisites:

- Python 2.6 or newer

- pip installed (http://docs.python-guide.org/en/latest/starting/install/linux/)

- virtualenv installed (pip install virtalenv)

- Git installed (yum install git-core / apt-get install git)

### Environment preparation:

- Create a virtual environment somewhere, e.g. in ~/venv (virtualenv ~/venv)

- Activate the virtual environment (source ~/venv/bin/activate)

- Change to the test/acceptance folder of the project

- Install the requirements for the acceptance tests in the virtual environment (pip install -r requirements.txt --allow-all-external).

### Tests execution:

- Change to the test/acceptance folder of the project if not already on it

- Run lettuce_tools with appropriate params (see available ones with the -h option)
