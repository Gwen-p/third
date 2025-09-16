<!-- lib/CreatePoll.svelte -->
<script>
    // Import API configuration
    import { getContext } from 'svelte';
    import { API_BASE } from '../config.js';

    const currentUser = getContext('currentUser');

    // Reactive variable for new poll question
    let newPollQuestion = '';

    // Reactive array for poll options
    let options = [''];

    // Reactive variable for valid until date
    let validUntil = '';

    // Reactive variable for feedback messages
    let message = '';

    // Reactive variable for loading state
    let isLoading = false;

    // Function to add a new option field
    function addOption() {
        // Add empty string to options array
        options = [...options, ''];
    }

    // Function to remove an option field
    function removeOption(index) {
        // Filter out the option at the specified index
        options = options.filter((_, i) => i !== index);
    }

    // Function to handle option input change
    function handleOptionChange(index, value) {
        // Update the option at the specified index
        options = options.map((opt, i) => i === index ? value : opt);
    }

    // Function to calculate default date (7 days from now)
    function getDefaultDate() {
        const date = new Date();
        date.setDate(date.getDate() + 7);
        return date.toISOString().split('T')[0];
    }

    // Initialize validUntil with default date
    $: if (!validUntil) {
        validUntil = getDefaultDate();
    }

    // Function to create a new poll
    async function createPoll() {
        // Check if question is not empty
        if (!newPollQuestion.trim()) {
            message = 'Poll question cannot be empty!';
            return;
        }

        // Check if valid until date is selected
        if (!validUntil) {
            message = 'Please select a valid until date!';
            return;
        }

        // Filter out empty options
        const validOptions = options.filter(opt => opt.trim());

        // Check if there are at least 2 options
        if (validOptions.length < 2) {
            message = 'Poll must have at least 2 options!';
            return;
        }

        // Set loading state to true
        isLoading = true;

        try {
            // Determine the user ID (either 'unknown' or actual user ID)
            const userId = $currentUser === 'unknown' ? 'unknown' : $currentUser;

            // Convert date string to ISO format
            const validUntilDate = new Date(validUntil + 'T23:59:59');

            // Send POST request to create poll API endpoint using API_BASE
            const response = await fetch(`${API_BASE}/polls/${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    question: newPollQuestion,
                    options: validOptions.map((caption, index) => ({
                        caption: caption,
                        presentationOrder: index + 1
                    })),
                    validUntil: validUntilDate.toISOString()
                })
            });

            // Check if response is successful
            if (response.ok) {
                // Parse response JSON data
                const newPoll = await response.json();

                // Show success message
                message = `Poll "${newPollQuestion}" created successfully!`;

                // Reset form fields
                newPollQuestion = '';
                options = [''];
                validUntil = getDefaultDate();
            } else {
                // Parse error response
                const error = await response.json();

                // Show error message
                message = error.message || 'Failed to create poll!';
            }
        } catch (error) {
            // Log any network errors
            console.error('Error creating poll:', error);
            message = 'Network error. Please try again.';
        } finally {
            // Set loading state to false regardless of outcome
            isLoading = false;
        }
    }
</script>

<div class="component">
    <h2>Create Polls</h2>

    {#if $currentUser === 'unknown'}
        <p>Only users can create new polls.</p>
    {:else }
        <div class="form-group">
            <label for="question">Poll Question:</label>
            <input
                    id="question"
                    type="text"
                    bind:value={newPollQuestion}
                    placeholder="Enter your question"
                    disabled={isLoading}
            />
        </div>

        <div class="form-group">
            <label for="validUntil">Valid Until:</label>
            <p>Select until when this poll will be active:</p>
            <input
                    id="validUntil"
                    type="date"
                    bind:value={validUntil}
                    min={new Date().toISOString().split('T')[0]}
                    disabled={isLoading}
            />
        </div>

        <h3>Options:</h3>

        <!-- Iterate over options array to create input fields -->
        {#each options as _, index}
            <div class="option-group">
                <input
                        type="text"
                        bind:value={options[index]}
                        on:input={(e) => handleOptionChange(index, e.target.value)}
                        placeholder={`Option ${index + 1}`}
                        disabled={isLoading}
                />

                <!-- Show remove button if there are more than one option -->
                {#if options.length > 1}
                    <button on:click={() => removeOption(index)} class="btn-remove" disabled={isLoading}>Remove</button>
                {/if}
            </div>
        {/each}

        <button on:click={addOption} class="btn-secondary" disabled={isLoading}>Add Option</button>
        <button on:click={createPoll} class="btn-primary" disabled={isLoading}>
            {#if isLoading}
                Creating...
            {:else}
                Create Poll
            {/if}
        </button>

        <!-- Display message if exists -->
        {#if message}
            <p class:success={message.includes('successfully')} class:error={!message.includes('successfully')}>{message}</p>
        {/if}
    {/if}
</div>