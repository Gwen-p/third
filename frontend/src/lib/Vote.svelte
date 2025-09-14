<script>
    let polls = [];
    let selectedPoll = null;
    let selectedOption = null;
    let username = '';
    let message = '';
    let votes = [];

    async function fetchPolls() {
        try {
            const response = await fetch('http://localhost:8080/polls');
            polls = await response.json();
            message = '';
        } catch (error) {
            message = 'Error charging polls';
        }
    }

    async function fetchVotes() {
        if (!selectedPoll) return;

        try {
            const response = await fetch(`http://localhost:8080/votes/${selectedPoll.id}`);
            votes = await response.json();
        } catch (error) {
            console.error('Error fetching votes:', error);
        }
    }

    async function vote() {
        if (!selectedPoll || selectedOption === null) return;

        try {
            let url;
            if (username.trim()) {
                url = `http://localhost:8080/votes/${selectedPoll.id}/${selectedOption}/${username}`;
            } else {
                url = `http://localhost:8080/votes/${selectedPoll.id}/${selectedOption}`;
            }

            const response = await fetch(url, {
                method: 'POST'
            });

            if (response.ok) {
                message = 'Succeeded vote!';
                selectedPoll = null;
                selectedOption = null;
                username = '';
                fetchPolls();
            } else {
                const errorData = await response.text();
                message = `Vote error: ${errorData}`;
            }
        } catch (error) {
            message = 'Connexion error';
        }
    }

    $: if (selectedPoll) {
        fetchVotes();
    }
</script>

<div class="vote-container">
    <h2>Vote poll</h2>
    <button on:click={fetchPolls}>Charge polls</button>

    {#if polls.length > 0}
        <div class="polls-list">
            <h3>Available polls:</h3>
            {#each polls as poll}
                <div class="poll-item">
                    <h4>{poll.question}</h4>
                    <p>Valid until: {new Date(poll.validUntil).toLocaleString()}</p>
                    <button on:click={() => selectedPoll = poll}>
                        Select poll
                    </button>
                </div>
            {/each}
        </div>
    {/if}

    {#if selectedPoll}
        <div class="poll-vote">
            <h3>{selectedPoll.question}</h3>
            <p>Valid until: {new Date(selectedPoll.validUntil).toLocaleString()}</p>

            <label>
                User name (optional):
                <input type="text" bind:value={username} placeholder="Keep empty for anonymous vote" />
            </label>

            <h4>Options:</h4>
            {#each selectedPoll.options as option, index}
                <label class="option-label">
                    <input
                            type="radio"
                            name="pollOption"
                            value={index+1}
                            bind:group={selectedOption}
                    />
                    {option.caption}
                </label>
            {/each}

            <button on:click={vote} disabled={selectedOption === null}>
                Vote
            </button>

            <h4>Actual votes:</h4>
            {#if votes.length > 0}
                <ul>
                    {#each votes as vote}
                        <li>
                            User: {vote.user ? vote.user.username : 'Anonymous'} -
                            Vote: {selectedPoll.options[vote.option]?.caption}
                        </li>
                    {/each}
                </ul>
            {:else}
                <p>There are no votes yet</p>
            {/if}
        </div>
    {/if}

    {#if message}
        <p class="{message.includes('Error') ? 'error' : 'success'}">{message}</p>
    {/if}
</div>

<style>
    .vote-container {
        margin: 20px;
        padding: 20px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }

    .polls-list {
        margin-top: 20px;
    }

    .poll-item {
        border: 1px solid #eee;
        padding: 10px;
        margin: 10px 0;
        border-radius: 5px;
    }

    .poll-vote {
        margin-top: 20px;
        padding: 15px;
        border: 1px solid #ddd;
        border-radius: 5px;
        background-color: #f9f9f9;
    }

    .option-label {
        display: block;
        margin: 5px 0;
        padding: 5px;
        cursor: pointer;
    }

    button, input {
        margin: 5px;
        padding: 8px;
    }

    .error { color: red; }
    .success { color: green; }
</style>