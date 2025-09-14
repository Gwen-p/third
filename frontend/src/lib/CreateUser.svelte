<script>
    let username = '';
    let email = '';
    let message = '';

    async function createUser() {
        try {
            const response = await fetch('http://localhost:8080/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, email })
            });

            if (response.ok) {
                message = 'Succeeded user creation!';
                username = '';
                email = '';
            } else if (response.status === 403) {
                message = 'User already exist';
            } else {
                message = 'Error creating user';
            }
        } catch (error) {
            message = 'Connection error';
        }
    }
</script>

<div class="user-form">
    <h2>Create user</h2>
    <form on:submit|preventDefault={createUser}>
        <input type="text" bind:value={username} placeholder="User name" required />
        <input type="email" bind:value={email} placeholder="Email" required />
        <button type="submit">Crear Usuario</button>
    </form>
    {#if message}
        <p class="{message.includes('Error') ? 'error' : 'success'}">{message}</p>
    {/if}
</div>

<style>
    .user-form {
        margin: 20px;
        padding: 20px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }
    input, button {
        margin: 5px;
        padding: 8px;
        display: block;
        width: 100%;
        max-width: 300px;
    }
    .error { color: red; }
    .success { color: green; }
</style>