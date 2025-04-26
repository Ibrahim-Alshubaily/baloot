import ChessGame from './components/ChessGame';
import './App.css';

function App() {
  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
    }}>
      <ChessGame />
    </div>
  );
}

export default App;
