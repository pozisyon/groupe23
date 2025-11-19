import React from "react";
import { Link } from "react-router-dom";

export const NotFoundPage: React.FC = () => {
  return (
    <div style={{ maxWidth: 480, margin: "3rem auto" }} className="card">
      <div className="card-title">404 - Page introuvable</div>
      <p className="mt-md text-muted">
        La page que tu cherches n&apos;existe pas. Retourne au lobby pour voir les parties Arbre32.
      </p>
      <div className="mt-md">
        <Link to="/lobby">
          <button className="btn">Retour au lobby</button>
        </Link>
      </div>
    </div>
  );
};