<!-- lib/UserSelector.svelte -->
<script>
    // Import Svelte lifecycle functions
    import { onMount } from 'svelte';

    // Import API configuration
    import { API_BASE } from '../config.js';

    // Import Svelte event dispatcher
    import { createEventDispatcher } from 'svelte';

    // Create event dispatcher
    const dispatch = createEventDispatcher();

    // Reactive variable to store all users
    let users = [];

    // Reactive variable for loading state
    let isLoading = false;

    // Reactive variable for error messages
    let error = '';

    // Prop for refresh function from parent
    export let refreshUsers;

    // Function to fetch all users from backend
    async function fetchUsers() {
        // Set loading state to true
        isLoading = true;

        // Clear any previous errors
        error = '';

        try {
            // Send GET request to users API endpoint using API_BASE
            const response = await fetch(`${API_BASE}/users`);

            // Check if response is successful
            if (response.ok) {
                // Parse response JSON data
                users = await response.json();
            } else {
                // Set error message if response is not successful
                error = 'Failed to load users. Please try again.';
                console.error('Failed to fetch users:', response.status);
            }
        } catch (err) {
            // Set error message for network errors
            error = 'Network error. Please check your connection.';
            console.error('Error fetching users:', err);
        } finally {
            // Set loading state to false regardless of outcome
            isLoading = false;
        }
    }

    // Function to handle user selection change
    function handleChange(event) {
        // Get the selected value from the dropdown
        const selectedValue = event.target.value;

        // find user if it is not unknow
        let selectedUser = null;
        if (selectedValue !== 'unknown') {
            selectedUser = users.find(user => user.username === selectedValue);
        }

        dispatch('select', {
            username: selectedValue,
            email: selectedUser,
            isUnknown: selectedValue === 'unknown'
        });
    }

    export let autoFetch = true;

    // Assign fetchUsers to the refreshUsers prop for parent to call
    $: refreshUsers = fetchUsers;

    // Modify the onMount
    onMount(() => {
        if (autoFetch) {
            fetchUsers();
        }
    });

    // Expose fetchUsers to parent component
    $: refreshUsers = fetchUsers;
</script>

<div class="user-selector">
    <label for="user-select">Select User:</label>

    <!-- Display loading state -->
    {#if isLoading}
        <select id="user-select" disabled>
            <option>Loading users...</option>
        </select>

        <!-- Display error state -->
    {:else if error}
        <select id="user-select" disabled>
            <option>{error}</option>
        </select>
        <button on:click={fetchUsers} class="btn-secondary">Retry</button>

        <!-- Display users dropdown -->
    {:else}
        <select id="user-select" on:change={handleChange}>
            <option value="unknown" selected>Unknown</option>

            <!-- Check if users array is not empty -->
            {#if users && users.length > 0}
                <!-- Iterate over users array to create options -->
                {#each users as user (user.username)}
                    <option value={user.username}>
                        {user.username}
                    </option>
                {/each}
            {:else}
                <option disabled>No users available</option>
            {/if}
        </select>
    {/if}
</div>