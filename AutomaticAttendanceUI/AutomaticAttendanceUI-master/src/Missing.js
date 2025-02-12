import React from 'react'
import MessagePage from './MessagePage/MessagePage'

const Missing = ({ role }) => {
    return (
        <MessagePage
            title={"Uh oh it seems you are lost!"}
            titleColor={"#D32F2F"}
            message={"Please go back home where you are safe!"}
            role={role}
        />
    )
}

export default Missing