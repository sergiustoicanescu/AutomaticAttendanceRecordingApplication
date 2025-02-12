/**
 * Validates the uniqueness of session names, start/end times, and overlapping sessions.
 * @param {Array} sessions - The list of sessions to validate.
 * @param {Function} setValidationError - Function to set the validation error message.
 * @returns {Object} An object containing a boolean flag indicating if there's an error and an array of error flags for each session.
 */
const validateSessions = (sessions, setValidationError) => {
    let hasError = false;
    const updatedSessionErrors = new Array(sessions.length).fill(false);

    const sessionNames = new Set();
    for (let i = 0; i < sessions.length; i++) {
        const session = sessions[i];
        const sessionNameLower = session.name.toLowerCase();
        if (sessionNames.has(sessionNameLower)) {
            updatedSessionErrors[i] = true;
            hasError = true;
        } else {
            sessionNames.add(sessionNameLower);
        }
    }
    if (hasError) {
        setValidationError('Each session must have a unique name.');
        return { hasError, updatedSessionErrors };
    }

    for (let i = 0; i < sessions.length; i++) {
        const session = sessions[i];
        const { startTime, endTime } = session;
        if (startTime && endTime && startTime >= endTime) {
            updatedSessionErrors[i] = true;
            hasError = true;
        }
    }
    if (hasError) {
        setValidationError('Start time must be earlier than end time.');
        return { hasError, updatedSessionErrors };
    }

    return { hasError: false, updatedSessionErrors };
};


export default validateSessions;
