
// Determine environment (default to development)
//const ENV = process.env.NODE_ENV || 'development';
/*
// API configuration for different environments
const API_CONFIG = {
    development: {
        API_BASE: 'http://localhost:3000/api' // Your local backend
    },
    production: {
        API_BASE: 'https://yourdomain.com/api' // Your production backend
    },
    test: {
        API_BASE: 'http://localhost:3001/api' // Test environment
    }
};

*/

// Get the current API base URL
export const API_BASE = 'http://localhost:8080';
    //API_CONFIG[ENV].API_BASE;

// Export the entire config if needed
//export default API_CONFIG;

