import React, { useCallback, useEffect, useState } from 'react';
import { createSlot, getMySlots, updateSlot, deleteSlot } from '../../api/slotApi';
import useToast from '../../hooks/useToast';

const STATUS_BADGES = {
  AVAILABLE: { bg: '#dcfce7', color: '#15803d', icon: 'bi-check-circle-fill', label: 'Available' },
  BOOKED: { bg: '#dbeafe', color: '#1d4ed8', icon: 'bi-bookmark-check-fill', label: 'Booked' },
};

export default function DoctorSlotsPage() {
  const toast = useToast();

  // State for slot list
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);

  // State for filters
  const [filterDate, setFilterDate] = useState('');
  const [filterStatus, setFilterStatus] = useState('');

  // State for Create Slot Form
  const [formDate, setFormDate] = useState('');
  const [formStart, setFormStart] = useState('');
  const [formEnd, setFormEnd] = useState('');
  const [createLoading, setCreateLoading] = useState(false);

  // State for Edit Slot Modal
  const [editModal, setEditModal] = useState({ open: false, slot: null });
  const [editDate, setEditDate] = useState('');
  const [editStart, setEditStart] = useState('');
  const [editEnd, setEditEnd] = useState('');
  const [editLoading, setEditLoading] = useState(false);

  // State for Delete Confirmation Modal
  const [deleteModal, setDeleteModal] = useState({ open: false, id: null });
  const [deleteLoading, setDeleteLoading] = useState(false);

  // Fetch slots from API
  const fetchSlots = useCallback(async () => {
    setLoading(true);
    try {
      const params = {};
      if (filterDate) params.slot_date = filterDate;
      if (filterStatus) params.slot_status = filterStatus;

      const res = await getMySlots(params);
      setSlots(res.data || []);
    } catch (err) {
      if (!err.toasted) {
        toast.error('Failed to load availability slots.');
      }
    } finally {
      setLoading(false);
    }
  }, [filterDate, filterStatus]);

  useEffect(() => {
    fetchSlots();
  }, [fetchSlots]);

  // Create slot handler
  const handleCreateSlot = async (e) => {
    e.preventDefault();
    if (!formDate || !formStart || !formEnd) {
      toast.error('Please fill in all fields.');
      return;
    }
    if (formEnd <= formStart) {
      toast.error('End time must be after start time.');
      return;
    }

    setCreateLoading(true);
    try {
      await createSlot({
        date: formDate,
        start_time: formStart,
        end_time: formEnd,
      });
      toast.success('Availability slot created successfully.');
      // Reset form
      setFormDate('');
      setFormStart('');
      setFormEnd('');
      fetchSlots();
    } catch (err) {
      if (!err.toasted) {
        toast.error(err.response?.data?.message || 'Failed to create availability slot.');
      }
    } finally {
      setCreateLoading(false);
    }
  };

  // Open Edit Modal
  const openEditModal = (slot) => {
    setEditModal({ open: true, slot });
    setEditDate(slot.date);
    setEditStart(slot.start_time);
    setEditEnd(slot.end_time);
  };

  const closeEditModal = () => {
    setEditModal({ open: false, slot: null });
  };

  // Update slot handler
  const handleUpdateSlot = async (e) => {
    e.preventDefault();
    if (!editDate || !editStart || !editEnd) {
      toast.error('Please fill in all fields.');
      return;
    }
    if (editEnd <= editStart) {
      toast.error('End time must be after start time.');
      return;
    }

    setEditLoading(true);
    try {
      await updateSlot(editModal.slot.id, {
        date: editDate,
        start_time: editStart,
        end_time: editEnd,
      });
      toast.success('Slot updated successfully.');
      closeEditModal();
      fetchSlots();
    } catch (err) {
      if (!err.toasted) {
        toast.error(err.response?.data?.message || 'Failed to update slot.');
      }
    } finally {
      setEditLoading(false);
    }
  };

  // Open Delete Modal
  const openDeleteModal = (id) => {
    setDeleteModal({ open: true, id });
  };

  const closeDeleteModal = () => {
    setDeleteModal({ open: false, id: null });
  };

  // Delete slot handler
  const handleDeleteSlot = async () => {
    if (!deleteModal.id) return;
    setDeleteLoading(true);
    try {
      await deleteSlot(deleteModal.id);
      toast.success('Availability slot deleted successfully.');
      closeDeleteModal();
      fetchSlots();
    } catch (err) {
      if (!err.toasted) {
        toast.error(err.response?.data?.message || 'Failed to delete slot.');
      }
    } finally {
      setDeleteLoading(false);
    }
  };

  // Format date helper
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-IN', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  // Timezone-safe local date string helper (YYYY-MM-DD)
  const getLocalDateString = () => {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // Check if a slot's date/time is in the past
  const isSlotPast = (slot) => {
    const todayStr = getLocalDateString();
    if (slot.date < todayStr) return true;
    if (slot.date === todayStr) {
      try {
        const [h, m] = slot.end_time.split(':').map(Number);
        const slotEndTime = new Date();
        slotEndTime.setHours(h, m, 0, 0);
        return new Date() > slotEndTime;
      } catch {
        return false;
      }
    }
    return false;
  };

  // Format time helper (24h to 12h)
  const formatTime = (time24) => {
    if (!time24) return '';
    try {
      const [hourStr, minStr] = time24.split(':');
      const hour = parseInt(hourStr, 10);
      const ampm = hour >= 12 ? 'PM' : 'AM';
      const formattedHour = hour % 12 || 12;
      return `${formattedHour}:${minStr} ${ampm}`;
    } catch {
      return time24;
    }
  };

  // Stats calculation
  const totalSlots = slots.filter((s) => !isSlotPast(s)).length;
  const availableSlotsCount = slots.filter((s) => s.status === 'AVAILABLE' && !isSlotPast(s)).length;
  const bookedSlotsCount = slots.filter((s) => s.status === 'BOOKED' && !isSlotPast(s)).length;

  return (
    <div>
      {/* Page Header */}
      <div className="page-header mb-4">
        <h1>Manage Availability</h1>
        <p>Define and schedule your consultation slots for patients to book.</p>
      </div>

      {/* Stats Cards */}
      <div className="row g-3 mb-4">
        <div className="col-12 col-md-4">
          <div className="card border-0 shadow-sm p-4 d-flex flex-row align-items-center gap-3">
            <div className="rounded-circle d-flex align-items-center justify-content-center text-primary" style={{ width: '48px', height: '48px', bg: 'rgba(13, 110, 253, 0.1)', backgroundColor: '#eef2ff' }}>
              <i className="bi bi-calendar-check-fill" style={{ fontSize: '1.5rem', color: '#4f46e5' }} />
            </div>
            <div>
              <h3 className="fw-bold mb-0 text-dark">{totalSlots}</h3>
              <span className="text-muted small">Total Slots</span>
            </div>
          </div>
        </div>

        <div className="col-12 col-md-4">
          <div className="card border-0 shadow-sm p-4 d-flex flex-row align-items-center gap-3">
            <div className="rounded-circle d-flex align-items-center justify-content-center text-success" style={{ width: '48px', height: '48px', backgroundColor: '#ecfdf5' }}>
              <i className="bi bi-check-circle-fill" style={{ fontSize: '1.5rem', color: '#10b981' }} />
            </div>
            <div>
              <h3 className="fw-bold mb-0 text-dark">{availableSlotsCount}</h3>
              <span className="text-muted small">Available Slots</span>
            </div>
          </div>
        </div>

        <div className="col-12 col-md-4">
          <div className="card border-0 shadow-sm p-4 d-flex flex-row align-items-center gap-3">
            <div className="rounded-circle d-flex align-items-center justify-content-center text-info" style={{ width: '48px', height: '48px', backgroundColor: '#eff6ff' }}>
              <i className="bi bi-bookmark-check-fill" style={{ fontSize: '1.5rem', color: '#3b82f6' }} />
            </div>
            <div>
              <h3 className="fw-bold mb-0 text-dark">{bookedSlotsCount}</h3>
              <span className="text-muted small">Booked Slots</span>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        {/* Left Side: Create Form */}
        <div className="col-12 col-lg-4">
          <div className="card border-0 shadow-sm p-4">
            <h5 className="fw-bold text-dark mb-3">Add Availability Slot</h5>
            <form onSubmit={handleCreateSlot}>
              <div className="mb-3">
                <label className="form-label fw-600 small text-muted">Select Date</label>
                <input
                  type="date"
                  className="form-control"
                  required
                  min={getLocalDateString()}
                  value={formDate}
                  onChange={(e) => setFormDate(e.target.value)}
                />
              </div>

              <div className="row g-2 mb-3">
                <div className="col-6">
                  <label className="form-label fw-600 small text-muted">Start Time</label>
                  <input
                    type="time"
                    className={`form-control ${formStart && formEnd && formEnd <= formStart ? 'is-invalid' : ''}`}
                    required
                    value={formStart}
                    onChange={(e) => setFormStart(e.target.value)}
                  />
                </div>
                <div className="col-6">
                  <label className="form-label fw-600 small text-muted">End Time</label>
                  <input
                    type="time"
                    className={`form-control ${formStart && formEnd && formEnd <= formStart ? 'is-invalid' : ''}`}
                    required
                    value={formEnd}
                    onChange={(e) => setFormEnd(e.target.value)}
                  />
                </div>
              </div>

              {formStart && formEnd && formEnd <= formStart && (
                <div className="text-danger small mb-3 d-flex align-items-center gap-1">
                  <i className="bi bi-exclamation-circle" />
                  End time must be after start time.
                </div>
              )}

              <button
                type="submit"
                className="btn btn-primary w-100 rounded-pill py-2 fw-600"
                disabled={createLoading || (formStart && formEnd && formEnd <= formStart)}
              >
                {createLoading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" />
                    Adding Slot...
                  </>
                ) : (
                  <>
                    <i className="bi bi-plus-circle me-1" />
                    Add Slot
                  </>
                )}
              </button>
            </form>
          </div>
        </div>

        {/* Right Side: Filters & Slots List */}
        <div className="col-12 col-lg-8">
          <div className="card border-0 shadow-sm p-4">
            {/* Filter Bar */}
            <div className="d-flex flex-wrap gap-2 align-items-center justify-content-between mb-4 border-bottom pb-3">
              <h5 className="fw-bold text-dark mb-0">My Slots</h5>

              <div className="d-flex gap-2">
                {/* Date Filter */}
                <input
                  type="date"
                  className="form-control form-control-sm"
                  style={{ maxWidth: '160px' }}
                  value={filterDate}
                  onChange={(e) => setFilterDate(e.target.value)}
                />

                {/* Status Filter */}
                <select
                  className="form-select form-select-sm"
                  style={{ maxWidth: '140px' }}
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                >
                  <option value="">All Statuses</option>
                  <option value="AVAILABLE">Available</option>
                  <option value="BOOKED">Booked</option>
                </select>

                {(filterDate || filterStatus) && (
                  <button
                    className="btn btn-sm btn-outline-secondary rounded-pill"
                    onClick={() => {
                      setFilterDate('');
                      setFilterStatus('');
                    }}
                  >
                    Clear
                  </button>
                )}
              </div>
            </div>

            {/* Loading Slots */}
            {loading && (
              <div className="d-flex flex-column align-items-center justify-content-center py-5">
                <div className="spinner-border text-primary mb-3" style={{ width: '2.5rem', height: '2.5rem' }} role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
                <p className="text-muted">Loading availability slots...</p>
              </div>
            )}

            {/* Empty Slots List */}
            {!loading && slots.length === 0 && (
              <div className="text-center py-5">
                <i className="bi bi-calendar-x text-muted d-block mb-3 animate-pulse" style={{ fontSize: '3rem', opacity: 0.3 }} />
                <h6 className="fw-bold text-dark mb-1">No Slots Found</h6>
                <p className="text-muted small mb-0">
                  {filterDate || filterStatus
                    ? 'No slots match the selected filters.'
                    : 'You have not defined any availability slots yet.'}
                </p>
              </div>
            )}

            {/* Slots Cards List */}
            {!loading && slots.length > 0 && (
              <div className="row g-3">
                {slots.map((slot) => {
                  const badge = STATUS_BADGES[slot.status] || STATUS_BADGES.AVAILABLE;
                  const isBooked = slot.status === 'BOOKED';
                  const isPast = isSlotPast(slot);
                  const isDisabled = isBooked || isPast;

                  return (
                    <div key={slot.id} className="col-12 col-md-6">
                      <div className="card border-0 p-3 hover-lift" style={{ backgroundColor: '#f8fafc', borderRadius: '12px', opacity: isPast ? 0.75 : 1 }}>
                        <div className="d-flex align-items-center justify-content-between mb-3">
                          <span
                            className="badge rounded-pill px-3 py-1 fw-600 d-flex align-items-center gap-1"
                            style={{
                              background: isPast ? '#f3f4f6' : badge.bg,
                              color: isPast ? '#6b7280' : badge.color,
                              fontSize: '0.75rem'
                            }}
                          >
                            <i className={`bi ${isPast ? 'bi-clock-history' : badge.icon}`} />
                            {isPast ? 'Past Slot' : badge.label}
                          </span>

                          {/* Action Buttons */}
                          <div className="d-flex gap-1">
                            <button
                              className="btn btn-sm btn-outline-primary rounded-circle border-0"
                              onClick={() => openEditModal(slot)}
                              disabled={isDisabled}
                              title={isBooked ? 'Booked slots cannot be edited.' : isPast ? 'Past slots cannot be edited.' : 'Edit Slot'}
                              style={{ width: '32px', height: '32px', padding: 0 }}
                            >
                              <i className="bi bi-pencil" />
                            </button>
                            <button
                              className="btn btn-sm btn-outline-danger rounded-circle border-0"
                              onClick={() => openDeleteModal(slot.id)}
                              disabled={isDisabled}
                              title={isBooked ? 'Booked slots cannot be deleted.' : isPast ? 'Past slots cannot be deleted.' : 'Delete Slot'}
                              style={{ width: '32px', height: '32px', padding: 0 }}
                            >
                              <i className="bi bi-trash" />
                            </button>
                          </div>
                        </div>

                        <div className="d-flex align-items-center gap-2 mb-2 text-dark small">
                          <i className="bi bi-calendar3 text-muted" />
                          <span className="fw-600">{formatDate(slot.date)}</span>
                        </div>

                        <div className="d-flex align-items-center gap-2 text-dark small">
                          <i className="bi bi-clock text-muted" />
                          <span className="fw-600">
                            {formatTime(slot.start_time)} – {formatTime(slot.end_time)}
                          </span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Edit Slot Modal */}
      {editModal.open && (
        <>
          <div className="modal-backdrop fade show" onClick={closeEditModal} />
          <div className="modal fade show d-block" tabIndex={-1} role="dialog">
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '16px' }}>
                <div className="modal-header border-0 pb-0">
                  <h5 className="modal-title fw-bold text-dark">Edit Availability Slot</h5>
                  <button type="button" className="btn-close" onClick={closeEditModal} />
                </div>
                <form onSubmit={handleUpdateSlot}>
                  <div className="modal-body pt-3">
                    <div className="mb-3">
                      <label className="form-label fw-600 small text-muted">Slot Date</label>
                      <input
                        type="date"
                        className="form-control"
                        required
                        min={getLocalDateString()}
                        value={editDate}
                        onChange={(e) => setEditDate(e.target.value)}
                      />
                    </div>

                    <div className="row g-2 mb-2">
                      <div className="col-6">
                        <label className="form-label fw-600 small text-muted">Start Time</label>
                        <input
                          type="time"
                          className={`form-control ${editStart && editEnd && editEnd <= editStart ? 'is-invalid' : ''}`}
                          required
                          value={editStart}
                          onChange={(e) => setEditStart(e.target.value)}
                        />
                      </div>
                      <div className="col-6">
                        <label className="form-label fw-600 small text-muted">End Time</label>
                        <input
                          type="time"
                          className={`form-control ${editStart && editEnd && editEnd <= editStart ? 'is-invalid' : ''}`}
                          required
                          value={editEnd}
                          onChange={(e) => setEditEnd(e.target.value)}
                        />
                      </div>
                    </div>

                    {editStart && editEnd && editEnd <= editStart && (
                      <div className="text-danger small d-flex align-items-center gap-1">
                        <i className="bi bi-exclamation-circle" />
                        End time must be after start time.
                      </div>
                    )}
                  </div>
                  <div className="modal-footer border-0 pt-0">
                    <button
                      type="button"
                      className="btn btn-outline-secondary rounded-pill px-4"
                      onClick={closeEditModal}
                      disabled={editLoading}
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="btn btn-primary rounded-pill px-4 fw-600"
                      disabled={editLoading || (editStart && editEnd && editEnd <= editStart)}
                    >
                      {editLoading ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2" />
                          Saving...
                        </>
                      ) : (
                        'Save Changes'
                      )}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </>
      )}

      {/* Delete Confirmation Modal */}
      {deleteModal.open && (
        <>
          <div className="modal-backdrop fade show" onClick={closeDeleteModal} />
          <div className="modal fade show d-block" tabIndex={-1} role="dialog">
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '16px' }}>
                <div className="modal-header border-0 pb-0">
                  <h5 className="modal-title fw-bold d-flex align-items-center gap-2">
                    <i className="bi bi-exclamation-triangle-fill text-danger" />
                    Delete Availability Slot
                  </h5>
                  <button type="button" className="btn-close" onClick={closeDeleteModal} />
                </div>
                <div className="modal-body pt-3">
                  <p className="text-muted mb-0">
                    Are you sure you want to delete this availability slot? Patients will no longer be able to book this session.
                  </p>
                </div>
                <div className="modal-footer border-0 pt-3">
                  <button
                    className="btn btn-outline-secondary rounded-pill px-4"
                    onClick={closeDeleteModal}
                    disabled={deleteLoading}
                  >
                    Cancel
                  </button>
                  <button
                    className="btn btn-danger rounded-pill px-4 fw-600"
                    onClick={handleDeleteSlot}
                    disabled={deleteLoading}
                  >
                    {deleteLoading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" />
                        Deleting...
                      </>
                    ) : (
                      'Yes, Delete'
                    )}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
