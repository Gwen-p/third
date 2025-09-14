<script>
    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();
    export let selectedUser = '';

    let username = '';
    let email = '';
    let message = '';

    async function createUser() {
        if (selectedUser !== '') {
            message = 'Need to be as Unknow to create users';
            return;
        }

        try {
            const response = await fetch(`/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, email })
            });

            if (response.ok) {
                message = 'User created successfully!';
                username = '';
                email = '';
                dispatch('userCreated');
            } else if (response.status === 403) {
                message = 'User already exists';
            } else {
                message = 'Error creating a user';
            }
        } catch (error) {
            message = 'Connection error';
        }
    }
</script>

<div class="user-form">
    <h2>Create user</h2>

    {#if selectedUser !== ''}
        <p class="warning">Need to be Unknown to create users</p>
    {:else}
        <p class="info">Creation of users</p>
    {/if}

    <form on:submit|preventDefault={createUser}>
        <input
                type="text"
                bind:value={username}
                placeholder="User name"
                required
                disabled={selectedUser !== ''}
        />
        <input
                type="email"
                bind:value={email}
                placeholder="Email"
                required
                disabled={selectedUser !== ''}
        />
        <button type="submit" disabled={selectedUser !== ''}>
            Create user
        </button>
    </form>
    {#if message}
        <p class="{message.includes('Error') || message.includes('Debes') ? 'error' : 'success'}">{message}</p>
    {/if}
</div>

<style>
    .user-form {
        margin: 20px;
        padding: 20px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }

    .warning {
        color: orange;
        font-weight: bold;
        background-color: #fff8e1;
        padding: 10px;
        border-radius: 4px;
        border: 1px solid #ffd54f;
    }

    .info {
        color: #666;
        background-color: #e3f2fd;
        padding: 10px;
        border-radius: 4px;
        border: 1px solid #bbdefb;
    }

    input, button {
        margin: 5px;
        padding: 8px;
        display: block;
        width: 100%;
        max-width: 300px;
    }

    input:disabled, button:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }

    .error {
        color: red;
        background-color: #ffebee;
        padding: 10px;
        border-radius: 4px;
        border: 1px solid #ffcdd2;
    }

    .success {
        color: green;
        background-color: #e8f5e8;
        padding: 10px;
        border-radius: 4px;
        border: 1px solid #c8e6c9;
    }
</style>