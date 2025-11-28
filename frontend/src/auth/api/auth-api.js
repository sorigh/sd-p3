import { HOST } from "../../commons/hosts";
import { performRequest } from "../../commons/api/rest-client";

// Define API endpoints 
const endpoint = {
  login: `${HOST.auth_service}/auth/login`,   
  register: `${HOST.auth_service}/auth/register`,  
};

// Function to log in a user
function loginUser(credentials, callback) {
  const request = new Request(endpoint.login, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(credentials),
  });

  performRequest(request, callback);
}

// Function to register a new user
function registerUser(userData, callback) {
  const request = new Request(endpoint.register, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(userData),
  });

  performRequest(request, callback);
}

// Export the functions
export { loginUser, registerUser };