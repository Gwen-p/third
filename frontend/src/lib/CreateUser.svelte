<!-- lib/CreateUser.svelte -->
<script>
    // Import Svelte event dispatcher
    import { getContext } from 'svelte';

    import { createEventDispatcher } from 'svelte';

    // Import API configuration
    import { API_BASE } from '../config.js';

    const currentUser = getContext('currentUser');

    // Create event dispatcher
    const dispatch = createEventDispatcher();

    // Reactive variable for new user name
    let newUserName = '';

    // Reactive variable for new user email
    let newUserEmail = '';

    // Reactive variable for feedback messages
    let message = '';

    // Reactive variable for loading state
    let isLoading = false;

    // Function to handle user creation
    async function createUser() {
        // Check if username is not empty
        if (!newUserName.trim()) {
            message = 'Username cannot be empty!';
            return;
        }

        // Set loading state to true
        isLoading = true;
        message = '';

        try {
            // Send POST request to create user API endpoint using API_BASE
            const response = await fetch(`${API_BASE}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: newUserName,
                    email: newUserEmail})
            });

            // Check if response is successful
            if (response.ok) {
                // Parse response JSON data
                const newUser = await response.json();

                // Show success message
                message = `User "${newUserName}" created successfully!`;

                // Dispatch event with the created user data
                dispatch('user-created',  { user: newUser });

                // Reset both fields
                newUserName = '';
                newUserEmail = '';
            } else {
                // Parse error response
                const error = await response.json();

                // Show error message
                message = error.message || 'Failed to create user!';
            }
        } catch (error) {
            // Log any network errors
            console.error('Error creating user:', error);
            message = 'Network error. Please try again.';
        } finally {
            // Set loading state to false regardless of outcome
            isLoading = false;
        }
    }
</script>

<div class="component">
    <h2>Create Users</h2>

    <!-- Only show user creation form when current user is "unknown" -->
    {#if $currentUser === 'unknown'}
        <div class="form-group">
            <label for="username">Username:</label>
            <input
                    id="username"
                    type="text"
                    bind:value={newUserName}
                    placeholder="Enter username"
                    disabled={isLoading}
            />
        </div>
        <div class="form-group">
            <label for="username">Email:</label>
            <input
                    type="email"
                    id="email"
                    bind:value={newUserEmail}
                    placeholder="Enter email"
                    disabled={isLoading}
            />
        </div>

        <button on:click={createUser} class="btn-primary" disabled={isLoading}>
            {#if isLoading}
                Creating...
            {:else}
                Create User
            {/if}
        </button>

        <!-- Display message if exists -->
        {#if message}
            <p class:success={message.includes('successfully')} class:error={!message.includes('successfully')}>{message}</p>
        {/if}
    {:else}
        <p>Only Unknown can create new users.</p>
    {/if}
</div>