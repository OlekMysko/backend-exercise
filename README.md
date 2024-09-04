# Tor Exit Nodes Checker

## Service Description

This service checks whether a provided IPv4 address is a [Tor](https://www.torproject.org/) exit node.

### Made Assumptions

1. **Tor exit nodes list is fetched from**: `https://check.torproject.org/exit-addresses`
2. **Refresh rate for Tor exit nodes list**: Defined in the configuration file (default is 1 hour).

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.3.3
- **Spring Data Redis**: For caching
- **Docker**: For containerization
- **Docker Compose**: To orchestrate the multi-container architecture

## How to Build the Service

1. **Clone the repository**:
    ```bash
    git clone https://github.com/ynd-consult-ug/backend-exercise-aleksander-mysko
    cd backend-exercise-aleksander-mysko
    ```

2. **Build the project using Maven**:
    ```bash
    mvn clean package
    ```

## How to Run Automatic Tests

Run the following command to execute the tests:
```bash
mvn test
```

## How to Run the Service Locally

**Prerequisites**:
- Ensure [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) are installed on your machine.
- Ensure Redis is running, which will be automatically handled by Docker Compose.

To run the service locally:

1. **Build and start the containers**:
    ```bash
    sudo docker-compose up --build
    ```

This will start a Redis instance and the Tor Exit Node Checker service.

## API Endpoints

### Check if an IP is a Tor exit node (HEAD request)

**Endpoint**: `HEAD /ip/{ip}`
- **Description**: Verifies if the provided IP address is a Tor exit node.
- **Path Parameter**: `ip` - The IP address to be checked.
- **Responses**:
   - `200 OK` - The IP address is a Tor exit node.
   - `400 Bad Request` - The IP address is not valid.
   - `404 Not Found` - The IP address is not a Tor exit node.

### Get information about an IP (GET request)

**Endpoint**: `GET /ip/{ip}`
- **Description**: Provides information if the provided IP address is a Tor exit node.
- **Path Parameter**: `ip` - The IP address to be checked.
- **Responses**:
   - `200 OK` - The IP address is a Tor exit node.
     ```json
     {
       "ipAddress": "string",
       "isTorExitNode": true,
       "message": "IP address is a Tor exit node"
     }
     ```
   - `400 Bad Request` - The IP address is not valid.
     ```json
     {
       "ipAddress": "string",
       "isTorExitNode": false,
       "message": "IP address is not valid"
     }
     ```
   - `404 Not Found` - The IP address is not a Tor exit node.
     ```json
     {
       "ipAddress": "string",
       "isTorExitNode": false,
       "message": "IP address is not a Tor exit node"
     }
     ```

## Improvements
- Create an endpoint to refresh Tor exit nodes.
- Optimize the parsing function to handle larger datasets more efficiently.
- Add comprehensive exception handling to ensure network failures and other errors are handled gracefully and throw appropriate custom exceptions where necessary.

## Built this app with just enough duct tape and code to get it running - optimization? Maybe in the next version, if coffee and time permit!