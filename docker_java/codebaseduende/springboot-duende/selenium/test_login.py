import requests

resp = requests.post("http://localhost:8080/login", json={"username": "domiuser1", "password": "secret123"})
print(resp.status_code)
print(resp.text)
