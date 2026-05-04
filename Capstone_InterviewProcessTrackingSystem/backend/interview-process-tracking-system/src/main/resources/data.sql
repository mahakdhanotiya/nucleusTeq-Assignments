-- Fix the check constraint on candidate_profiles to include EVALUATED status
-- This runs on every startup but is safe (drops and recreates)
-- Fix the check constraint on candidate_profiles to include EVALUATED status
ALTER TABLE candidate_profiles DROP CONSTRAINT IF EXISTS candidate_profiles_application_status_check;

ALTER TABLE candidate_profiles ADD CONSTRAINT candidate_profiles_application_status_check
    CHECK (application_status IN ('APPLIED', 'PROFILING_COMPLETED', 'INTERVIEW_SCHEDULED', 'EVALUATED', 'SELECTED', 'REJECTED'));
