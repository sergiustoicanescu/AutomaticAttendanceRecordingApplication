import React, { useState } from 'react'
import './EditableCommentCell.css'

const EditableCommentCell = ({ params, status, onCommentSave }) => {
    const [editValue, setEditValue] = useState(params.value || '');

    const handleKeyDown = (event) => {
        if (event.key === ' ') {
            event.stopPropagation();
        }
    };

    const handleSave = () => {
        if (editValue !== params.value) {
            onCommentSave(params.row.email, editValue);
        }
    }

    const handleChange = (e) => {
        const value = e.target.value;
        if (value.length <= 255) {
            setEditValue(value);
        }
    };

    return (
        <div className='comment-cell'>
            <textarea
                value={editValue}
                onChange={handleChange}
                onBlur={() => handleSave()}
                onKeyDown={handleKeyDown}
                disabled={status == null}
                style={{ width: '100%', marginRight: '8px' }}
            />
        </div>
    );
};

export default EditableCommentCell