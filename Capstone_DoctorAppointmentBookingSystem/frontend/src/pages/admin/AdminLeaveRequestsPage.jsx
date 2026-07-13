import React, { useEffect, useState } from 'react';
import { getLeaveRequests, approveLeaveRequest, rejectLeaveRequest } from '../../api/appointmentApi';
import useToast from '../../hooks/useToast';
import Spinner from '../../components/common/Spinner';

export default function AdminLeaveRequestsPage() {
  const toast = useToast();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [processingId, setProcessingId] = useState(null);
  const [filterStatus, setFilterStatus] = useState('');

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const params = filterStatus ? { status: filterStatus } : {};
      const res = await getLeaveRequests(params);
      setRequests(res.data || []);
    } catch (err) {
      toast.error('Failed to load leave requests.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, [filterStatus]);

  const handleApprove = async (id) => {
    try {
      setProcessingId(id);
      await approveLeaveRequest(id);
      toast.success('Leave request approved and slots cancelled successfully!');
      fetchRequests();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to approve request.');
    } finally {
      setProcessingId(null);
    }
  };

  const handleReject = async (id) => {
    try {
      setProcessingId(id);
      await rejectLeaveRequest(id);
      toast.success('Leave request rejected.');
      fetchRequests();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to reject request.');
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <div className="container-fluid py-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3 mb-4">
        <div>
          <h1 className="h3 mb-1 text-dark d-flex align-items-center gap-2">
            Doctor Leave Requests 📝
          </h1>
          <p className="text-muted mb-0">Manage doctor requests for schedule leave.</p>
        </div>

        {/* Filter Dropdown */}
        <div style={{ minWidth: '180px' }}>
          <select
            className="form-select rounded-pill shadow-sm"
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
          >
            <option value="">All Request Statuses</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>
        </div>
      </div>

      {loading ? (
        <Spinner label="Loading leave requests..." />
      ) : requests.length === 0 ? (
        <div className="card border-0 shadow-sm text-center py-5" style={{ borderRadius: '12px' }}>
          <div className="py-4">
            <i className="bi bi-file-earmark-x text-muted" style={{ fontSize: '3rem' }}></i>
            <h5 className="mt-3 text-secondary">No Leave Requests Found</h5>
          </div>
        </div>
      ) : (
        <div className="card border-0 shadow-sm overflow-hidden" style={{ borderRadius: '12px' }}>
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th className="px-4">Practitioner</th>
                  <th>Target Date</th>
                  <th>Start Time</th>
                  <th>End Time</th>
                  <th>Reason</th>
                  <th>Request Status</th>
                  <th className="text-end px-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {requests.map((req) => {
                  const statusColors = {
                    PENDING: 'bg-warning text-dark',
                    APPROVED: 'bg-success text-white',
                    REJECTED: 'bg-danger text-white',
                  };
                  return (
                    <tr key={req.id}>
                      <td className="px-4">
                        <div className="fw-600 text-dark">{req.doctor_name}</div>
                        <div className="text-muted small" style={{ fontSize: '0.75rem' }}>{req.doctor_email}</div>
                      </td>
                      <td className="fw-600">{req.date}</td>
                      <td>
                        <span className="badge bg-light text-dark border fw-600">{req.start_time}</span>
                      </td>
                      <td>
                        <span className="badge bg-light text-dark border fw-600">{req.end_time}</span>
                      </td>
                      <td className="text-muted small" style={{ maxWidth: '250px' }}>{req.reason}</td>
                      <td>
                        <span className={`badge rounded-pill px-3 py-1.5 fw-600 ${statusColors[req.status] || 'bg-secondary'}`}>
                          {req.status}
                        </span>
                      </td>
                      <td className="text-end px-4">
                        {req.status === 'PENDING' && (
                          <div className="d-flex align-items-center justify-content-end gap-2">
                            <button
                              className="btn btn-success btn-sm rounded-pill px-3 fw-600"
                              disabled={processingId !== null}
                              onClick={() => handleApprove(req.id)}
                            >
                              {processingId === req.id ? '...' : 'Approve'}
                            </button>
                            <button
                              className="btn btn-outline-danger btn-sm rounded-pill px-3 fw-600"
                              disabled={processingId !== null}
                              onClick={() => handleReject(req.id)}
                            >
                              {processingId === req.id ? '...' : 'Reject'}
                            </button>
                          </div>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
