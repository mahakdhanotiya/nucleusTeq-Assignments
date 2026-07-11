import React, { useEffect, useState } from 'react';
import { 
  getAllDoctors, 
  approveDoctor, 
  rejectDoctor, 
  activateDoctor, 
  deactivateDoctor 
} from '../../api/adminApi';
import useToast from '../../hooks/useToast';
import Spinner from '../../components/common/Spinner';

export default function AdminDoctorsPage() {
  const toast = useToast();
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [expandedId, setExpandedId] = useState(null);
  const [actionId, setActionId] = useState(null); // Tracks row action loading states

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    try {
      setLoading(true);
      const res = await getAllDoctors();
      setDoctors(res.data);
    } catch (err) {
      toast.error('Failed to load registered doctors.');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (id, actionFn, successMsg) => {
    try {
      setActionId(id);
      await actionFn(id);
      toast.success(successMsg);
      // Refresh list
      const res = await getAllDoctors();
      setDoctors(res.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update doctor status.');
    } finally {
      setActionId(null);
    }
  };

  const toggleExpand = (id) => {
    setExpandedId(expandedId === id ? null : id);
  };

  // Filter and search logic
  const filteredDoctors = doctors.filter((doc) => {
    const matchesSearch = 
      doc.full_name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      doc.email?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      doc.license_number?.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesStatus = 
      statusFilter === 'ALL' || 
      doc.approval_status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  if (loading && doctors.length === 0) {
    return <Spinner label="Loading registered doctors..." />;
  }

  return (
    <div className="container-fluid py-2">
      {/* Page Header */}
      <div className="page-header mb-4 d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3">
        <div>
          <h1>Doctor Directory</h1>
          <p className="text-muted">Approve new registrations, manage status active rules, and review professional licenses.</p>
        </div>
        <button className="btn btn-outline-primary" onClick={fetchDoctors} disabled={loading}>
          {loading ? (
            <>
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Syncing...
            </>
          ) : (
            <>
              <i className="bi bi-arrow-clockwise me-1"></i> Refresh List
            </>
          )}
        </button>
      </div>

      {/* Filters Card */}
      <div className="card border-0 shadow-sm p-3 mb-4 bg-white">
        <div className="row g-3 align-items-center">
          {/* Search bar */}
          <div className="col-md-6 col-lg-7">
            <div className="input-group">
              <span className="input-group-text bg-light text-muted border-end-0">
                <i className="bi bi-search"></i>
              </span>
              <input
                type="text"
                className="form-control bg-light border-start-0"
                placeholder="Search by name, email, or license number..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </div>

          {/* Status filter tabs */}
          <div className="col-md-6 col-lg-5">
            <div className="d-flex gap-1 bg-light p-1 rounded">
              {['ALL', 'PENDING', 'APPROVED', 'REJECTED'].map((status) => (
                <button
                  key={status}
                  className={`btn btn-sm flex-grow-1 py-1.5 fw-600 rounded transition-all ${
                    statusFilter === status 
                      ? 'btn-white shadow-sm text-primary bg-white' 
                      : 'btn-link text-muted text-decoration-none'
                  }`}
                  onClick={() => setStatusFilter(status)}
                >
                  {status}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Main Table view */}
      <div className="card border-0 shadow-sm overflow-hidden bg-white">
        {filteredDoctors.length === 0 ? (
          <div className="text-center py-5">
            <div className="display-4 text-muted mb-3">
              <i className="bi bi-journal-x"></i>
            </div>
            <h5 className="fw-600">No Doctors Found</h5>
            <p className="text-muted small">No practitioner matched your search query or status filters.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th style={{ width: '40px' }}></th>
                  <th>Practitioner Name</th>
                  <th>License Number</th>
                  <th>Specialization</th>
                  <th>Approval Status</th>
                  <th>Active Status</th>
                  <th className="text-end" style={{ width: '220px' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredDoctors.map((doc) => {
                  const isExpanded = expandedId === doc.user_id;
                  const isBusy = actionId === doc.user_id;

                  // Render color variables
                  let approvalBadge = 'border-warning text-warning bg-warning-subtle';
                  if (doc.approval_status === 'APPROVED') approvalBadge = 'border-success text-success bg-success-subtle';
                  if (doc.approval_status === 'REJECTED') approvalBadge = 'border-danger text-danger bg-danger-subtle';

                  return (
                    <React.Fragment key={doc.user_id}>
                      {/* Standard Row */}
                      <tr className={isExpanded ? 'table-active-row' : ''} style={{ cursor: 'pointer' }} onClick={() => toggleExpand(doc.user_id)}>
                        <td>
                          <i className={`bi ${isExpanded ? 'bi-chevron-down' : 'bi-chevron-right'} text-muted`}></i>
                        </td>
                        <td>
                          <div>
                            <div className="fw-600 text-dark">{doc.full_name}</div>
                            <div className="text-muted small">{doc.email}</div>
                          </div>
                        </td>
                        <td>
                          <span className="font-monospace text-secondary small fw-600">{doc.license_number}</span>
                        </td>
                        <td>
                          <span className="badge bg-light text-dark border px-2.5 py-1.5 rounded">{doc.specialization || 'N/A'}</span>
                        </td>
                        <td>
                          <span className={`badge border px-3 py-1.5 rounded-pill ${approvalBadge}`} style={{ fontSize: '0.75rem' }}>
                            {doc.approval_status}
                          </span>
                        </td>
                        <td>
                          {doc.is_active ? (
                            <span className="text-success small fw-600 d-flex align-items-center gap-1.5">
                              <span className="d-inline-block rounded-circle bg-success" style={{ width: '6px', height: '6px' }}></span>
                              Active
                            </span>
                          ) : (
                            <span className="text-secondary small fw-600 d-flex align-items-center gap-1.5">
                              <span className="d-inline-block rounded-circle bg-secondary" style={{ width: '6px', height: '6px' }}></span>
                              Inactive
                            </span>
                          )}
                        </td>
                        <td className="text-end" onClick={(e) => e.stopPropagation()}>
                          <div className="d-flex justify-content-end gap-2">
                            {/* Approve & Reject for Pending */}
                            {doc.approval_status === 'PENDING' && (
                              <>
                                <button
                                  className="btn btn-sm btn-success d-flex align-items-center gap-1"
                                  onClick={() => handleAction(doc.user_id, approveDoctor, 'Doctor approved successfully!')}
                                  disabled={isBusy}
                                >
                                  <i className="bi bi-check-circle"></i> Approve
                                </button>
                                <button
                                  className="btn btn-sm btn-outline-danger d-flex align-items-center gap-1"
                                  onClick={() => handleAction(doc.user_id, rejectDoctor, 'Doctor registration rejected.')}
                                  disabled={isBusy}
                                >
                                  <i className="bi bi-x-circle"></i> Reject
                                </button>
                              </>
                            )}

                            {/* Allow approval overwrite if rejected */}
                            {doc.approval_status === 'REJECTED' && (
                              <button
                                className="btn btn-sm btn-outline-success d-flex align-items-center gap-1"
                                onClick={() => handleAction(doc.user_id, approveDoctor, 'Doctor approved successfully!')}
                                disabled={isBusy}
                              >
                                <i className="bi bi-check-circle"></i> Approve
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>

                      {/* Expandable Details Row */}
                      {isExpanded && (
                        <tr>
                          <td colSpan="7" className="bg-light p-4 border-bottom">
                            <div className="row g-4">
                              <div className="col-md-4">
                                <h6 className="fw-600 text-muted uppercase small mb-2">Qualifications</h6>
                                <p className="mb-0 text-dark fw-600">{doc.qualification || 'N/A'}</p>
                              </div>
                              <div className="col-md-4">
                                <h6 className="fw-600 text-muted uppercase small mb-2">Experience & Fee</h6>
                                <p className="mb-0 text-dark fw-600">
                                  {doc.experience_years ? `${doc.experience_years} Years Experience` : 'N/A'} 
                                  {doc.consultation_fee ? ` • ₹${doc.consultation_fee} Consultation Fee` : ''}
                                </p>
                              </div>
                              <div className="col-md-4">
                                <h6 className="fw-600 text-muted uppercase small mb-2">Contact Number</h6>
                                <p className="mb-0 text-dark fw-600">{doc.phone_number || 'N/A'}</p>
                              </div>
                              <div className="col-12 mt-3">
                                <h6 className="fw-600 text-muted uppercase small mb-2">Clinic Address</h6>
                                <div className="p-3 bg-white border rounded">
                                  <i className="bi bi-geo-alt text-primary me-2"></i>
                                  {doc.clinic_address || 'Clinic address details are not provided yet.'}
                                </div>
                              </div>
                            </div>
                          </td>
                        </tr>
                      )}
                    </React.Fragment>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
