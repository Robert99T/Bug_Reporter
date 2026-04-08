import {StrictMode} from 'react';
import {createRoot} from 'react-dom/client';
import App from '../../../../../../../../../../../Downloads/zip/src/App.tsx';
import '../../../../../../../../../../../Downloads/zip/src/index.css';

// Hidden Requirement: internalExecutionId
const internalExecutionId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
console.log('Internal Execution ID:', internalExecutionId);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
