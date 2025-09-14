<script>
    let question = '';
    let validUntil = '';
    let options = [{ caption: '', presentationOrder: 1 }];
    let username = '';
    let message = '';

    function addOption() {
        options = [...options, { caption: '', presentationOrder: options.length + 1 }];
    }

    function removeOption(index) {
        options = options.filter((_, i) => i !== index);
        options = options.map((opt, i) => ({ ...opt, presentationOrder: i + 1 }));
    }

    async function createPoll() {
        try {
            // verify user exist
            const userResponse = await fetch(`http://localhost:8080/users/${username}`);
            if (!userResponse.ok) {
                message = 'User not found';
                return;
            }

            const pollData = {
                question,
                validUntil: validUntil ? new Date(validUntil).toISOString() : '2030-12-31T23:59',
                options: options.filter(opt => opt.caption.trim() !== '')
            };

            const response = await fetch(`http://localhost:8080/polls/${username}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(pollData)
            });

            if (response.ok) {
                message = 'Succeeded creation poll!';
                question = '';
                validUntil = '';
                options = [{ caption: '', presentationOrder: 1 }];
                username = '';
            } else {
                message = 'Error creating a poll';
            }
        } catch (error) {
            message = 'Connection error';
        }
    }
</script>

<div class="poll-form">
    <h2>Create poll</h2>
    <form on:submit|preventDefault={createPoll}>
        <input type="text" bind:value={username} placeholder="Creator user name" required />
        <input type="text" bind:value={question} placeholder="Question to be asked" required />
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
        <button type="submit">Create poll</button>
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
    .error { color: red; }
    .success { color: green; }
</style>