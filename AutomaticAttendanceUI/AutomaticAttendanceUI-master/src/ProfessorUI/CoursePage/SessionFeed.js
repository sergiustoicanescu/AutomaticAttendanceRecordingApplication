import React from 'react'
import Session from './Session/Session'

const SessionFeed = ({ course, sessions, setSessions }) => {
    return (
        <div className='session-block'>
            {sessions.map(session => (
                <Session key={session.id} course={course} session={session} setSessions={setSessions} />
            ))}
        </div>
    )
}

export default SessionFeed