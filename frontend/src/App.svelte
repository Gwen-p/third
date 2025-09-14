<script>
    import CreateUser from './lib/CreateUser.svelte';
    import CreatePoll from './lib/CreatePoll.svelte';
    import Vote from './lib/Vote.svelte';
    import UserSelector from './lib/UserSelector.svelte';

    let currentView = 'vote';
    let selectedUser = '';
    let users = [];

    function handleUserCreated() {
        // Forzar actualización de la lista de usuarios
        setTimeout(() => {
            const userSelector = document.querySelector('user-selector');
            if (userSelector && userSelector.updateUsers) {
                userSelector.updateUsers();
            }
        }, 100);
    }

    function handleUsersUpdated() {
        fetchUsers();
    }

    async function fetchUsers() {
        try {
            const response = await fetch('/users');
            if (response.ok) {
                users = await response.json();
            }
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    }
</script>

<main>
    <h1>Sistema de Encuestas - DAT250</h1>

    <!-- Selector de usuarios -->
    <UserSelector
            bind:selectedUser
            bind:users
            on:usersUpdated={handleUsersUpdated}
    />

    <nav class="navigation">
        <button class:active={currentView === 'user'} on:click={() => currentView = 'user'}>
            Create user
        </button>
        <button
                class:active={currentView === 'poll'}
                on:click={() => {
                if (selectedUser === '') {
                    alert('User needed to create polls');
                    return;
                }
                currentView = 'poll';
            }}
                disabled={selectedUser === ''}
        >
            Polls
        </button>
        <button class:active={currentView === 'vote'} on:click={() => currentView = 'vote'}>
            Vote
        </button>
    </nav>

    <div class="content">
        {#if currentView === 'user'}
            <CreateUser on:userCreated={handleUserCreated} {selectedUser} />
        {:else if currentView === 'poll'}
            <CreatePoll {selectedUser} {users} />
        {:else}
            <Vote {selectedUser} {users} />
        {/if}
    </div>
</main>

<style>
    main {
        max-width: 1000px;
        margin: 0 auto;
        padding: 20px;
        font-family: Arial, sans-serif;
    }

    .navigation {
        display: flex;
        justify-content: center;
        margin-bottom: 30px;
        gap: 10px;
    }

    .navigation button {
        padding: 12px 20px;
        border: 2px solid #007acc;
        background: white;
        color: #007acc;
        cursor: pointer;
        border-radius: 5px;
        font-weight: bold;
        transition: all 0.3s;
    }

    .navigation button:hover:not(:disabled) {
        background: #007acc;
        color: white;
    }

    .navigation button.active {
        background: #007acc;
        color: white;
    }

    .navigation button:disabled {
        opacity: 0.5;
        cursor: not-allowed;
        border-color: #ccc;
        color: #ccc;
    }

    .content {
        min-height: 400px;
    }

    h1 {
        text-align: center;
        color: #333;
        margin-bottom: 30px;
    }
</style>