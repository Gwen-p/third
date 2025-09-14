<script>
    import CreateUser from './lib/CreateUser.svelte';
    import CreatePoll from './lib/CreatePoll.svelte';
    import Vote from './lib/Vote.svelte';

    let currentView = 'vote';
</script>

<main>
    <h1>Polls system</h1>

    <nav class="navigation">
        <button class:active={currentView === 'user'} on:click={() => currentView = 'user'}>
            Create User
        </button>
        <button class:active={currentView === 'poll'} on:click={() => currentView = 'poll'}>
            Create Poll
        </button>
        <button class:active={currentView === 'vote'} on:click={() => currentView = 'vote'}>
            Vote
        </button>
    </nav>

    <div class="content">
        {#if currentView === 'user'}
            <CreateUser />
        {:else if currentView === 'poll'}
            <CreatePoll />
        {:else}
            <Vote />
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

    .navigation button:hover {
        background: #007acc;
        color: white;
    }

    .navigation button.active {
        background: #007acc;
        color: white;
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