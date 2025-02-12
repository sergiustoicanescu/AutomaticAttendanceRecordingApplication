import React from 'react';
import { DataGrid } from '@mui/x-data-grid';
import Button from '@mui/material/Button';
import './StudentTable.css';
import EditableCommentCell from './EditableCommentCell';

const StudentTable = ({ students, onStatusChange, onCommentSave, activeEmail }) => {
    const columns = [
        { field: 'lastName', headerName: 'Last Name', headerClassName: 'columnHeaders', flex: 1 },
        { field: 'firstName', headerName: 'First Name', headerClassName: 'columnHeaders', flex: 1 },
        { field: 'email', headerName: 'Email', headerClassName: 'columnHeaders', flex: 2 },
        {
            field: 'status',
            headerName: 'Status',
            headerClassName: 'columnHeaders',
            flex: 2,
            renderCell: (params) => {
                const handleStatusChange = (newStatus) => {
                    onStatusChange(params.row.email, newStatus);
                };

                return (
                    <>
                        <Button onClick={() => handleStatusChange('PRESENT')} variant='contained' size="small" color="success" style={{ marginRight: '8px' }} disabled={activeEmail === params.row.email}>P</Button>
                        <Button onClick={() => handleStatusChange('ABSENT')} variant='contained' size="small" color="error" style={{ marginRight: '8px' }} disabled={activeEmail === params.row.email}>A</Button>
                        <Button onClick={() => handleStatusChange('AUTHORIZED_ABSENT')} variant='contained' size="small" color="warning" disabled={activeEmail === params.row.email}>AA</Button>
                    </>
                );
            }
        },
        { field: 'date', headerName: 'Date', headerClassName: 'columnHeaders', flex: 1 },
        { field: 'time', headerName: 'Time', headerClassName: 'columnHeaders', flex: 1 },
        { field: 'comment', headerName: 'Comment', headerClassName: 'columnHeaders', flex: 1.5, renderCell: (params) => <EditableCommentCell params={params} status={params.row.status} onCommentSave={onCommentSave} /> },
    ];

    const rows = students.map((student, index) => ({
        id: index,
        ...student,
        status: student.attendance.status,
        date: student.attendance.date,
        time: student.attendance.time ? student.attendance.time.substring(0, 5) : '',
        comment: student.attendance.comment || '',
    }));

    return (
        <div className="data-grid-container">
            <DataGrid
                rows={rows}
                columns={columns}
                pageSize={rows.length}
                pageSizeOptions={[100]}
                getRowClassName={(params) =>
                    params.row.status === 'PRESENT' ? 'status-present' :
                        params.row.status === 'ABSENT' ? 'status-absent' :
                            params.row.status === 'AUTHORIZED_ABSENT' ? 'status-authorised-absent' : ''
                }
            />
        </div>
    );

};

export default StudentTable;
