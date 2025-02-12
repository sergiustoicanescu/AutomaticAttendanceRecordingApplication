import React from 'react';
import {
    TbCircleLetterA, TbCircleLetterB, TbCircleLetterC, TbCircleLetterD, TbCircleLetterE,
    TbCircleLetterF, TbCircleLetterG, TbCircleLetterH, TbCircleLetterI, TbCircleLetterJ,
    TbCircleLetterK, TbCircleLetterL, TbCircleLetterM, TbCircleLetterN, TbCircleLetterO,
    TbCircleLetterP, TbCircleLetterQ, TbCircleLetterR, TbCircleLetterS, TbCircleLetterT,
    TbCircleLetterU, TbCircleLetterV, TbCircleLetterW, TbCircleLetterX, TbCircleLetterY,
    TbCircleLetterZ, TbCircle
} from "react-icons/tb";

const iconMapping = {
    'A': TbCircleLetterA, 'B': TbCircleLetterB, 'C': TbCircleLetterC, 'D': TbCircleLetterD,
    'E': TbCircleLetterE, 'F': TbCircleLetterF, 'G': TbCircleLetterG, 'H': TbCircleLetterH,
    'I': TbCircleLetterI, 'J': TbCircleLetterJ, 'K': TbCircleLetterK, 'L': TbCircleLetterL,
    'M': TbCircleLetterM, 'N': TbCircleLetterN, 'O': TbCircleLetterO, 'P': TbCircleLetterP,
    'Q': TbCircleLetterQ, 'R': TbCircleLetterR, 'S': TbCircleLetterS, 'T': TbCircleLetterT,
    'U': TbCircleLetterU, 'V': TbCircleLetterV, 'W': TbCircleLetterW, 'X': TbCircleLetterX,
    'Y': TbCircleLetterY, 'Z': TbCircleLetterZ
};

const CourseLogo = ({ name }) => {
    const match = name.match(/[a-zA-Z]/);
    const firstLetter = match ? match[0].toUpperCase() : null;
    const Icon = firstLetter ? iconMapping[firstLetter] : null;

    return Icon ? <Icon /> : <TbCircle />;
};

export default CourseLogo;
