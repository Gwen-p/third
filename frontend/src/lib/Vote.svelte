<script>
    export let selectedUser = '';
    export let users = [];

    let polls = [];
    let selectedPoll = null;
    let selectedOption = null;
    let message = '';
    let votes = [];
    let userPolls = [];
    let loading = false;

    async function fetchPolls() {
        loading = true;
        try {
            const response = await fetch(`/polls`);
            if (response.ok) {
                polls = await response.json();
                filterUserPolls();
            }
            message = '';
        } catch (error) {
            message = 'Error error charging polls';
        }
        loading = false;
    }

    async function fetchVotes() {
        if (!selectedPoll) return;

        try {
            const response = await fetch(`/votes/${selectedPoll.id}`);
            if (response.ok) {
                votes = await response.json();
            }
        } catch (error) {
            console.error('Error fetching votes:', error);
        }
    }

    function filterUserPolls() {
        if (selectedUser) {
            userPolls = polls.filter(poll => poll.creator && poll.creator.username === selectedUser);
        } else {
            userPolls = [];
        }
    }

    async function vote() {
        if (!selectedPoll || selectedOption === null) return;

        try {
            let url;
            if (selectedUser) {
                url = `/votes/${selectedPoll.id}/${selectedOption}/${selectedUser}`;
            } else {
                url = `/votes/${selectedPoll.id}/${selectedOption}`;
            }

            const response = await fetch(url, { method: 'POST' });

            if (response.ok) {
                message = 'Vote register successfully!';
                selectedPoll = null;
                selectedOption = null;
                fetchPolls();
            } else {
                const errorData = await response.text();
                message = `Connection error: ${errorData}`;
            }
        } catch (error) {
            message = 'C';
        }
    }

    async function deletePoll(pollId) {
        if (!confirm('Are you sure you want to delete the poll?')) return;

        try {
            const response = await fetch(`/polls/${pollId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                message = 'Poll deleted successfully!';
                fetchPolls();
            } else {
                message = 'Error deleting a poll';
            }
        } catch (error) {
            message = 'Connection error';
        }
    }

    async function changeVote(voteId, newOption) {
        try {
            const response = await fetch(`/votes/${selectedPoll.id}/${voteId}/${newOption}`, {
                method: 'PUT'
            });

            if (response.ok) {
                message = 'Vote modify successfully!';
                fetchVotes();
            } else {
                message = 'Error modifying vote';
            }
        } catch (error) {
            message = 'Connection error';
        }
    }

    $: if (selectedPoll) {
        fetchVotes();
    }

    $: if (selectedUser) {
        filterUserPolls();
    }

    $: if (polls.length > 0) {
        filterUserPolls();
    }
</script>

<div class="vote-container">
    <h2>Vote at polls</h2>

    {#if selectedUser}
        <p class="info">Actual user: <strong>{selectedUser}</strong></p>
    {:else}
        <p class="info">Anonymous mode</p>
    {/if}

    <button on:click={fetchPolls} disabled={loading}>
        {loading ? 'Charging...' : 'Polls actualized'}
    </button>

    {#if polls.length > 0}
        <!-- User polls -->
        {#if userPolls.length > 0}
            <div class="user-polls">
                <h3>Your polls:</h3>
                {#each userPolls as poll}
                    <div class="poll-item owned">
                        <h4>{poll.question}</h4>
                        <p>Valid until: {new Date(poll.validUntil).toLocaleString()}</p>
                        <p>Options: {poll.options.length}</p>
                        <button on:click={() => selectedPoll = poll}>See/Administrate</button>
                        <button on:click={() => deletePoll(poll.id)} class="danger">Delete</button>
                    </div>
                {/each}
            </div>
        {/if}

        <!-- All polls -->
        <div class="polls-list">
            <h3>All the polls:</h3>
            {#each polls as poll}
                <div class="poll-item">
                    <h4>{poll.question}</h4>
                    <p>Valid until: {new Date(poll.validUntil).toLocaleString()}</p>
                    <p>Created by: {poll.creator ? poll.creator.username : 'Unknow'}</p>
                    <button on:click={() => selectedPoll = poll}>Vote on this poll</button>
                </div>
            {/each}
        </div>
    {/if}

    {#if selectedPoll}
        <div class="poll-vote">
            <h3>{selectedPoll.question}</h3>
            <p>Valid until: {new Date(selectedPoll.validUntil).toLocaleString()}</p>
            <p>Created by: {selectedPoll.creator ? selectedPoll.creator.username : 'Unknow'}</p>

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
                <div class="votes-list">
                    {#each votes as vote}
                        <div class="vote-item">
                            <span>User: {vote.user ? vote.user.username : 'Unknow'}</span>
                            <span>Vote: {selectedPoll.options[vote.option]?.caption}</span>

                            {#if selectedUser && vote.user && vote.user.username === selectedUser}
                                <select on:change={(e) => changeVote(vote.id, parseInt(e.target.value))}>
                                    <option value="">Change vote...</option>
                                    {#each selectedPoll.options as option, index}
                                        <option value={index}>{option.caption}</option>
                                    {/each}
                                </select>
                            {/if}
                        </div>
                    {/each}
                </div>
            {:else}
                <p>No votes yet</p>
            {/if}

            <button on:click={() => selectedPoll = null}>Close</button>
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

    .info {
        color: #666;
        margin-bottom: 15px;
    }

    .user-polls {
        margin-bottom: 30px;
        padding: 15px;
        background-color: #f0f8ff;
        border-radius: 5px;
    }

    .poll-item {
        border: 1px solid #eee;
        padding: 15px;
        margin: 10px 0;
        border-radius: 5px;
    }

    .poll-item.owned {
        background-color: #f0fff0;
        border-color: #4caf50;
    }

    .poll-vote {
        margin-top: 20px;
        padding: 20px;
        border: 2px solid #007acc;
        border-radius: 5px;
        background-color: #f9f9f9;
    }

    .option-label {
        display: block;
        margin: 8px 0;
        padding: 8px;
        border: 1px solid #ddd;
        border-radius: 4px;
        cursor: pointer;
        background: white;
    }

    .option-label:hover {
        background: #f0f0f0;
    }

    .votes-list {
        margin-top: 15px;
    }

    .vote-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px;
        margin: 5px 0;
        border: 1px solid #eee;
        border-radius: 4px;
        background: white;
    }

    button, input {
        margin: 5px;
        padding: 8px;
    }

    .error { color: red; }
    .success { color: green; }
</style>