import React, { createRef } from 'react';
import './CodeInput.css';

const CodeInput = ({ code, setCode, type }) => {
    const inputRefs = Array(6).fill().map(() => createRef());

    const handleChange = (value, index) => {
        const newCode = [...code];
        newCode[index] = value.toUpperCase().replace(/[^A-Z0-9]/g, '');
        setCode(newCode);

        if (value && index < 5) {
            inputRefs[index + 1].current.focus();
        }
    };

    const handleKeyDown = (e, index) => {
        if (e.key === 'Backspace' && !code[index]) {
            index > 0 && inputRefs[index - 1].current.focus();
        }
    };

    return (
        <div className='code-input-container'>
            {code.map((char, index) => (
                <input
                    key={index}
                    type={type}
                    value={char}
                    onChange={(e) => handleChange(e.target.value, index)}
                    onKeyDown={(e) => handleKeyDown(e, index)}
                    ref={inputRefs[index]}
                    maxLength="1"
                    className="code-input"
                />
            ))}
        </div>
    );
};

export default CodeInput;
