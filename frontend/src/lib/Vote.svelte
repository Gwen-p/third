<!-- lib/Vote.svelte -->
<script>
    // Import API configuration
    import { getContext } from 'svelte';
    import { API_BASE } from '../config.js';

    const currentUser = getContext('currentUser');

    // Reactive variable to store polls data
    let polls = [];

    // Reactive variable to store user's current selections
    let selections = {};

    // Reactive variable for loading state
    let isLoading = false;
    let loadingPolls = false;

    // Function to fetch polls from backend
    async function fetchPolls() {
        loadingPolls = true;
        try {
            // Send GET request to polls API endpoint using API_BASE
            const response = await fetch(`${API_BASE}/polls`);

            // Check if response is successful
            if (response.ok) {
                // Parse response JSON data
                polls = await response.json();
                console.log('Polls loaded:', polls);
            } else {
                // Log error if response is not successful
                console.error('Failed to fetch polls');
            }
        } catch (error) {
            // Log any network errors
            console.error('Error fetching polls:', error);
        } finally {
            loadingPolls = false;
        }
    }

    // Function to handle voting
    async function vote(pollId, optionIndex) {
        // Set loading state to true
        isLoading = true;

        try {
            // Determine if user is unknown or registered
            const isUnknownUser = $currentUser === 'unknown';

            let response;

            if (isUnknownUser) {
                // Anonymous vote - use endpoint: /votes/{pollId}/{option}
                response = await fetch(`${API_BASE}/votes/${pollId}/${optionIndex}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
            } else {
                // Registered user vote - use endpoint: /votes/{pollId}/{option}/{userName}
                response = await fetch(`${API_BASE}/votes/${pollId}/${optionIndex}/${$currentUser}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
            }

            // Check if response is successful
            if (response.ok) {
                // Parse response JSON data
                const newVote = await response.json();
                console.log('Vote successful:', newVote);

                // Update selection for the poll
                selections[pollId] = optionIndex;

                // Refresh polls data to get updated vote counts
                fetchPolls();

                alert('Vote recorded successfully!');
            } else {
                // Parse error response
                const error = await response.json();

                // Show error message
                alert(error.message || 'Failed to vote!');
                console.error('Vote failed:', error);
            }
        } catch (error) {
            // Log any network errors
            console.error('Error voting:', error);
            alert('Network error. Please try again.');
        } finally {
            // Set loading state to false regardless of outcome
            isLoading = false;
        }
    }

    // Function to delete a poll (only for poll creator)
    async function deletePoll(pollId) {
        // Confirm deletion
        if (!confirm('Are you sure you want to delete this poll?')) return;

        // Set loading state to true
        isLoading = true;

        try {
            // Send DELETE request to poll API endpoint using API_BASE
            const response = await fetch(`${API_BASE}/polls/${pollId}`, {
                method: 'DELETE'
            });

            // Check if response is successful
            if (response.ok) {
                // Remove the poll from local state
                polls = polls.filter(poll => poll.id !== pollId);

                // Remove selection for deleted poll
                delete selections[pollId];

                alert('Poll deleted successfully!');
            } else {
                // Parse error response
                const error = await response.json();

                // Show error message
                alert(error.message || 'Failed to delete poll!');
            }
        } catch (error) {
            // Log any network errors
            console.error('Error deleting poll:', error);
            alert('Network error. Please try again.');
        } finally {
            // Set loading state to false regardless of outcome
            isLoading = false;
        }
    }

    // Function to get vote count for a specific option
    function getVoteCount(option) {
        // Count votes based on the votes array in the option
        return option.votes ? option.votes.length : 0;
    }

    // Function to check if current user is the creator of a poll
    function isPollCreator(poll) {
        // For unknown users, always return false (unknown users can't create polls)
        if ($currentUser === 'unknown') {
            return false;
        }

        // For registered users, check if they created the poll
        return poll.creator === $currentUser;
    }

    // Function to check if poll is expired
    function isPollExpired(poll) {
        const validUntil = new Date(poll.validUntil);
        const now = new Date();
        return now > validUntil;
    }

    // Function to check if user has already voted in a poll
    function hasUserVoted(poll) {
        if ($currentUser === 'unknown') {
            // For anonymous users, we can't track votes across sessions
            return selections[poll.id] !== undefined;
        }

        // For registered users, check if any option has their vote
        return poll.options.some(option =>
                option.votes && option.votes.some(vote =>
                    typeof vote === 'object' ? vote.user === $currentUser : false
                )
        );
    }

    // Load data when component is mounted
    fetchPolls();
</script>

<div class="component">
    <h2>Vote on Polls</h2>

    <!-- Display loading message if data is being fetched -->
    {#if loadingPolls}
        <p>Loading polls...</p>
    {:else if polls.length === 0}
        <p>No polls available. Create some polls first!</p>
    {:else}
        <!-- Iterate over each poll -->
        {#each polls as poll (poll.id)}
            <div class="poll" class:expired={isPollExpired(poll)}>
                <div class="poll-header">
                    <h3>{poll.question}</h3>

                    <!-- Show delete button if user is the creator of the poll -->
                    {#if isPollCreator(poll)}
                        <button on:click={() => deletePoll(poll.id)} class="btn-remove" disabled={isLoading}>
                            {isLoading ? 'Deleting...' : 'Delete Poll'}
                        </button>
                    {/if}
                    <div class="poll-meta">
                        <span class="creator">By: {poll.creator}</span>
                        <p><span class="valid-until">Valid until: {new Date(poll.validUntil).toLocaleDateString()}</span></p>
                            {#if isPollExpired(poll)}
                            <span class="expired-badge">EXPIRED</span>
                        {/if}
                    </div>

                </div>

                <!-- Show message if poll is expired -->
                {#if isPollExpired(poll)}
                    <p class="expired-message">This poll has expired and can no longer be voted on.</p>
                {:else if hasUserVoted(poll)}
                    <p class="already-voted">You have already voted in this poll.</p>
                {/if}

                <!-- Iterate over each option in the poll -->
                {#each poll.options as option, index (index)}
                    <div class="option">
                        <label>
                            <input
                                    type="radio"
                                    name={`poll-${poll.id}`}
                                    value={index}
                                    checked={selections[poll.id] === index}
                                    on:change={() => vote(poll.id, index + 1)}
                                    disabled={isLoading || isPollExpired(poll) || hasUserVoted(poll)}
                            />
                            {option.caption}
                        </label>

                        <!-- Display vote count for this option -->
                        <span class="vote-count">({getVoteCount(option)} votes)</span>

                        <!-- Show vote timeline if available -->
                        {#if option.votes && option.votes.length > 0}
                            <div class="vote-timeline">
                                {#each option.votes as voteTimestamp (voteTimestamp)}
                                    <span class="vote-time">
                                        {new Date(voteTimestamp).toLocaleTimeString()}
                                    </span>
                                {/each}
                            </div>
                        {/if}
                    </div>
                {/each}

                <!-- Display current selection if exists -->
                {#if selections[poll.id] !== undefined && !isPollExpired(poll)}
                    <p class="current-selection">
                        Your current selection: {poll.options[selections[poll.id]].caption}
                    </p>
                {/if}

                <!-- Total votes for this poll -->
                <div class="total-votes">
                    Total votes: {poll.options.reduce((total, option) => total + getVoteCount(option), 0)}
                </div>
            </div>
        {/each}
    {/if}
</div>