<script>

    export let selectedUser = '';
    export let users = [];

    let question = '';
    let validUntil = '';
    let options = [{ caption: '', presentationOrder: 1 }];
    let message = '';

    function addOption() {
        options = [...options, { caption: '', presentationOrder: options.length + 1 }];
    }

    function removeOption(index) {
        options = options.filter((_, i) => i !== index);
        options = options.map((opt, i) => ({ ...opt, presentationOrder: i + 1 }));
    }

    async function createPoll() {
        if (!selectedUser) {
            message = 'Select a user to create polls';
            return;
        }

        try {
            const pollData = {
                question,
                validUntil: validUntil ? new Date(validUntil).toISOString() : '2030-12-31T23:59:59Z',
                options: options.filter(opt => opt.caption.trim() !== '')
            };

            const response = await fetch(`/polls/${selectedUser}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(pollData)
            });

            if (response.ok) {
                message = 'Poll created successfully!';
                question = '';
                validUntil = '';
                options = [{ caption: '', presentationOrder: 1 }];
            } else {
                const errorText = await response.text();
                message = `Error creating a new poll: ${errorText}`;
            }
        } catch (error) {
            message = 'Connection error';
        }
    }
</script>

<div class="poll-form">
    <h2>Create polls</h2>

    {#if !selectedUser}
        <p class="warning">Select a user to create polls</p>
    {:else}
        <p class="info">Creating polls as: <strong>{selectedUser}</strong></p>
    {/if}

    <form on:submit|preventDefault={createPoll}>
        <input type="text" bind:value={question} placeholder="Question for the poll" required />
        <input type="datetime-local" bind:value={validUntil} placeholder="Valid until" />

        <h3>Options:</h3>
        {#each options as option, index}
            <div class="option-row">
                <input
                        type="text"
                        bind:value={option.caption}
                        placeholder={`Option ${index + 1}`}
                />
                {#if options.length > 1}
                    <button type="button" on:click={() => removeOption(index)}>Delete</button>
                {/if}
            </div>
        {/each}

        <button type="button" on:click={addOption}>Add option</button>
        <button type="submit" disabled={!selectedUser}>Create poll</button>
    </form>
    {#if message}
        <p class="{message.includes('Error') ? 'error' : 'success'}">{message}</p>
    {/if}
</div>

<style>
    .poll-form {
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
    }

    .option-row {
        display: flex;
        align-items: center;
        gap: 10px;
        margin: 5px 0;
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

    button:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }
</style>