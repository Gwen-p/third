<script>

    export let selectedUser = '';
    export let users = [];
    let loading = false;
    let error = '';

    async function fetchUsers() {
        loading = true;
        error = '';
        try {
            const response = await fetch(`/users`);
            if (response.ok) {
                users = await response.json();
            } else {
                error = 'Error charging users';
            }
        } catch (err) {
            error = 'Connection error';
        }
        loading = false;
    }

    $: if (users.length === 0) {
        fetchUsers();
    }
</script>

<div class="user-selector">
    <label>
        Actual user:
        <select bind:value={selectedUser}>
            <option value="">Unknown</option>
            {#each users as user}
                <option value={user.username}>{user.username} ({user.email})</option>
            {/each}
        </select>
    </label>

    {#if loading}
        <span class="loading">Charging users...</span>
    {/if}

    {#if error}
        <span class="error">{error}</span>
    {/if}

    <button on:click={fetchUsers} title="Refresh users">Refresh</button>
</div>

<style>
    .user-selector {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 20px;
        padding: 10px;
        background-color: #f5f5f5;
        border-radius: 5px;
    }

    select {
        padding: 5px;
        border: 1px solid #ccc;
        border-radius: 3px;
    }

    .loading {
        color: #666;
        font-style: italic;
    }

    .error {
        color: red;
    }

    button {
        padding: 5px 10px;
        border: 1px solid #ccc;
        background: white;
        border-radius: 3px;
        cursor: pointer;
    }
</style>