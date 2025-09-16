<!-- App.svelte -->
<script>
    // Import the writable store from Svelte for state management
    import { writable } from 'svelte/store';
    // Import setContext from Svelte to provide data to child components
    import { setContext } from 'svelte';

    // Import the UserSelector component for user selection
    import UserSelector from './lib/UserSelector.svelte';
    // Import the CreateUsers component for user creation
    import CreateUsers from './lib/CreateUser.svelte';
    // Import the CreatePolls component for poll creation
    import CreatePolls from './lib/CreatePoll.svelte';
    // Import the VotePolls component for voting functionality
    import VotePolls from './lib/Vote.svelte';

    // Create a writable store to track the current user, defaulting to 'unknown'
    const currentUser = writable('unknown');

    // Provide the currentUser store to all child components via context API
    setContext('currentUser', currentUser);

    // Reactive variable to track which view is currently active, defaulting to 'vote'
    let activeView = 'vote';

    // Store for refresh function from UserSelector
    let userSelectorRefresh = () => {};

    // Function to handle when a user is selected from the UserSelector component
    function handleUserSelect(event) {
        // Extract the user ID from the event details
        const { username, email, isUnknown } = event.detail;

        // Check if the selected user is 'unknown'
        if (isUnknown) {
            // Set the current user to 'unknown'
            currentUser.set('unknown');
        } else {
            // Set the current user to an object with the selected user's ID or username
            currentUser.set(username);
        }
    }

    // Function to switch between different views in the application
    function setView(view) {
        // Update the activeView variable to the new view
        activeView = view;
    }

    // Function to refresh the users list
    function refreshUsers() {
        // Call the refresh function from UserSelector
        if (userSelectorRefresh) {
            userSelectorRefresh();
            console.log('Refreshing user list...');
        }
    }
</script>

<!-- Set the page title in the document head -->
<svelte:head>
    <title>Polling App</title>
</svelte:head>

<!-- Main container for the application -->
<main>
    <!-- Header section containing the title and user selector -->
    <header>
        <!-- Main heading for the application -->
        <h1>Polling Application</h1>

        <!-- User selection component-->
        <UserSelector on:select={handleUserSelect} bind:refreshUsers={userSelectorRefresh} />
    </header>

    <!-- Navigation tabs for switching between different application views -->
    <nav class="tabs">
        <!-- Button to navigate to the Create Users view -->
        <button class="vanner-button"
                class:active={activeView === 'users'}
                on:click={() => setView('users')}
        >
            Create Users
        </button>

        <!-- Button to navigate to the Create Polls view -->
        <button class="vanner-button"
                class:active={activeView === 'polls'}
                on:click={() => setView('polls')}
        >
            Create Polls
        </button>

        <!-- Button to navigate to the Vote view -->
        <button class="vanner-button"
                class:active={activeView === 'vote'}
                on:click={() => setView('vote')}
        >
            Vote
        </button>
    </nav>

    <!-- Container for the main content that changes based on the active view -->
    <div class="content">
        <!-- Conditionally render the CreateUsers component when activeView is 'users' -->
        {#if activeView === 'users'}
            <CreateUsers on:user-created={refreshUsers} />

            <!-- Conditionally render the CreatePolls component when activeView is 'polls' -->
        {:else if activeView === 'polls'}
            <CreatePolls />

            <!-- Conditionally render the VotePolls component for any other activeView value -->
        {:else}
            <VotePolls />
        {/if}
    </div>
</main>