import React from 'react';

/**
 * ErrorBoundary catches JavaScript errors anywhere in its child component tree,
 * logs those errors, and displays a fallback UI instead of crashing the whole app.
 */
export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI.
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    // You can also log the error to an error reporting service
    console.error("ErrorBoundary caught an error:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="container py-5">
          <div className="card border-0 shadow-sm p-4 text-center bg-white">
            <div className="display-4 text-danger mb-3">
              <i className="bi bi-exclamation-triangle-fill"></i>
            </div>
            <h4 className="fw-600 text-dark">Something went wrong</h4>
            <p className="text-muted mb-4">We encountered an unexpected error while loading this page. Please try refreshing or returning to the dashboard.</p>
            <button 
              className="btn btn-primary px-4 py-2 fw-600 shadow-sm"
              onClick={() => {
                this.setState({ hasError: false });
                window.location.reload();
              }}
            >
              <i className="bi bi-arrow-clockwise me-1"></i> Reload Page
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
