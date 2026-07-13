import React, { useEffect, useState, useCallback, useRef } from 'react';
import { searchDoctors } from '../../api/doctorApi';
import DoctorCard from '../../components/patient/DoctorCard';
import useToast from '../../hooks/useToast';
import Spinner from '../../components/common/Spinner';

const SPECIALIZATIONS = [
  'Cardiologist', 'Dermatologist', 'Dentist', 'Neurologist',
  'Orthopedic', 'Pediatrician', 'General Physician', 'Gynecologist',
];

export default function PatientHomePage() {
  const toast = useToast();
  const toastRef = useRef(toast);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);

  // Search and filter states
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedSpecialization, setSelectedSpecialization] = useState('');

  // Debounced search trigger with race-condition prevention
  useEffect(() => {
    let active = true;

    const delayDebounceFn = setTimeout(async () => {
      setLoading(true);
      try {
        const params = {};
        const cleanName = searchTerm.trim().replace(/^Dr\.\s*/i, '').replace(/^Dr\s+/i, '');
        if (cleanName) {
          params.name = cleanName;
        }
        if (selectedSpecialization) {
          params.specialization = selectedSpecialization;
        }

        const res = await searchDoctors(params);
        if (active) {
          setDoctors(res.data || []);
        }
      } catch (err) {
        if (active && !err.toasted) {
          toastRef.current.error(err.response?.data?.message || 'Failed to load doctors list. Please try again.');
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }, 300);

    return () => {
      active = false;
      clearTimeout(delayDebounceFn);
    };
  }, [searchTerm, selectedSpecialization]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    // Re-trigger immediately if needed (debounced effect will handle it too)
  };

  const handleSpecializationClick = (spec) => {
    setSelectedSpecialization((prevSpec) => (prevSpec === spec ? '' : spec));
  };

  const handleResetFilters = () => {
    setSearchTerm('');
    setSelectedSpecialization('');
  };

  return (
    <div>
      {/* Page Header */}
      <div className="page-header d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h1>Find Your Specialist</h1>
          <p>Search and book online consultations with certified medical specialists.</p>
        </div>
        {(searchTerm || selectedSpecialization) && (
          <button
            className="btn btn-outline-secondary btn-sm rounded-pill px-3 align-self-start align-self-md-center"
            onClick={handleResetFilters}
          >
            <i className="bi bi-x-circle me-1" />
            Clear Filters
          </button>
        )}
      </div>

      {/* Search Bar & Filters Section */}
      <div className="card border-0 shadow-sm mb-4 p-4">
        <form onSubmit={handleSearchSubmit} className="row g-3">
          {/* Text Search Input */}
          <div className="col-12">
            <div className="input-group">
              <span className="input-group-text bg-white border-end-0">
                <i className="bi bi-search text-muted" />
              </span>
              <input
                type="text"
                className="form-control border-start-0 ps-0 py-2"
                placeholder="Search doctors by name (e.g. Dr. John Doe)..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              {searchTerm && (
                <button
                  className="btn btn-link text-muted position-absolute end-0 top-0 bottom-0 z-3 border-0 bg-transparent text-decoration-none"
                  type="button"
                  onClick={() => setSearchTerm('')}
                  style={{ marginRight: '10px' }}
                >
                  <i className="bi bi-x-lg" />
                </button>
              )}
            </div>
          </div>

          {/* Specialization Filter Pills */}
          <div className="col-12">
            <label className="form-label text-muted small fw-600 mb-2">Filter by Specialization</label>
            <div className="d-flex flex-wrap gap-2">
              <button
                type="button"
                className={`btn btn-sm rounded-pill px-3 py-1.5 fw-600 transition-all ${!selectedSpecialization
                    ? 'btn-primary'
                    : 'btn-outline-primary bg-white'
                  }`}
                onClick={() => setSelectedSpecialization('')}
              >
                All Specialists
              </button>
              {SPECIALIZATIONS.map((spec) => {
                const isActive = selectedSpecialization === spec;
                return (
                  <button
                    key={spec}
                    type="button"
                    className={`btn btn-sm rounded-pill px-3 py-1.5 fw-600 transition-all ${isActive
                        ? 'btn-primary'
                        : 'btn-outline-primary bg-white'
                      }`}
                    onClick={() => handleSpecializationClick(spec)}
                  >
                    {spec}
                  </button>
                );
              })}
            </div>
          </div>
        </form>
      </div>

      {/* Doctor Listings Grid */}
      {loading ? (
        <Spinner label="Searching for matching specialists..." />
      ) : doctors.length > 0 ? (
        <div className="row g-4">
          {doctors.map((doctor) => (
            <div key={doctor.user_id} className="col-12 col-md-6 col-lg-4">
              <DoctorCard doctor={doctor} />
            </div>
          ))}
        </div>
      ) : (
        /* Empty State */
        <div className="card border-0 shadow-sm p-5 text-center my-4">
          <div className="mb-3">
            <i className="bi bi-person-x text-muted" style={{ fontSize: '4rem' }} />
          </div>
          <h4 className="fw-600 mb-2">No Specialists Found</h4>
          <p className="text-muted mx-auto" style={{ maxWidth: '400px' }}>
            We couldn't find any doctors matching your search or filters. Try adjusting your search query or reset filters.
          </p>
          <button
            className="btn btn-primary rounded-pill px-4 mt-3"
            onClick={handleResetFilters}
          >
            Reset All Filters
          </button>
        </div>
      )}
    </div>
  );
}
