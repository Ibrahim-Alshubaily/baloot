import { useEffect, useState } from "react";
import { Chessboard } from "react-chessboard";

const INIT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
interface Move {
    from: string;
    to: string;
}

interface MakeMoveResponse {
    newFen: string;
    opponentMove: Move | null;
}


async function makeMove(fen: string, move: Move): Promise<MakeMoveResponse> {
    const res = await fetch("http://localhost:8080/chess/make-move", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ fen, move: move.from + move.to }),
    });
    return res.json();
}

function getBoardSize() {
    const width = window.innerWidth;
    const height = window.innerHeight;
    return Math.min(width, height) * 0.9;
}

export default function ChessGame() {
    const [fen, setFen] = useState(INIT_FEN);
    const [boardSize, setBoardSize] = useState(getBoardSize());

    useEffect(() => {
        function handleResize() {
            setBoardSize(getBoardSize());
        }
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    async function onDrop(sourceSquare: string, targetSquare: string) {
        const move = { from: sourceSquare, to: targetSquare };
        // const isLegal = legalMoves.some(
        //     (m) => m.from === move.from && m.to === move.to
        // );
        // if (!isLegal) return false;
        const { newFen } = await makeMove(fen, move);
        setFen(newFen);
        return true;
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <div style={{
                padding: '16px',
                background: 'linear-gradient(135deg, #1B2E3C, #243B53)',
                borderRadius: '16px',
                boxShadow: '0 4px 20px rgba(0,0,0,0.6)'
            }}>
                <Chessboard
                    position={fen === "start" ? "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1" : fen}
                    onPieceDrop={(sourceSquare, targetSquare, piece) => {
                        onDrop(sourceSquare, targetSquare);
                        return true;
                    }} boardWidth={boardSize - 32}
                    customDarkSquareStyle={{ backgroundColor: '#7A99B8' }}   // Dark blue
                    customLightSquareStyle={{ backgroundColor: '#DDE6F0' }}  // Light sky blue
                    boardStyle={{
                        borderRadius: '12px',
                        overflow: 'hidden',
                    }}
                />
            </div>
        </div>
    );
}
