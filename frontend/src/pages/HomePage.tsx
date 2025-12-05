import { motion } from "framer-motion";

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-green-900 via-green-800 to-green-700 text-white flex flex-col items-center justify-center px-6 py-10">

      {/* Titre principal */}
      <motion.h1
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1 }}
        className="text-5xl sm:text-6xl font-extrabold tracking-wide text-center drop-shadow-lg"
      >
        🌳 Arbre32
      </motion.h1>

      {/* Sous-titre poétique */}
      <motion.p
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.6, duration: 1 }}
        className="text-lg sm:text-xl mt-4 max-w-2xl text-center text-green-200 leading-relaxed"
      >
        Un jeu où chaque carte est une décision.
        Chaque décision, une branche.
        Et chaque branche, un chemin vers la victoire… ou la chute.
      </motion.p>

      {/* Zone visuelle — arbre + cartes flottantes */}
      <motion.div
        initial={{ opacity: 0, scale: 0.85 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 1, duration: 1 }}
        className="relative mt-12 w-72 h-72 sm:w-96 sm:h-96"
      >
        {/* Cercle lumineux */}
        <div className="absolute inset-0 bg-green-300/20 rounded-full blur-3xl"></div>

        {/* Tronc stylisé */}
        <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-6 h-32 bg-green-950 rounded-full shadow-lg"></div>

        {/* Feuillage */}
        <div className="absolute top-8 left-1/2 -translate-x-1/2 w-52 h-52 bg-green-600 rounded-full opacity-90 shadow-inner border border-green-500"></div>

        {/* Cartes animées */}
        <motion.div
          animate={{ y: [-4, 4, -4] }}
          transition={{ repeat: Infinity, duration: 3 }}
          className="absolute -top-6 left-10 w-16 h-24 bg-white text-black rounded-lg shadow-xl border flex items-center justify-center text-2xl"
        >
          ♣️ 7
        </motion.div>

        <motion.div
          animate={{ y: [4, -4, 4] }}
          transition={{ repeat: Infinity, duration: 3.2 }}
          className="absolute top-10 right-10 w-16 h-24 bg-white text-black rounded-lg shadow-xl border rotate-6 flex items-center justify-center text-2xl"
        >
          ♥️ A
        </motion.div>

        <motion.div
          animate={{ y: [-3, 3, -3] }}
          transition={{ repeat: Infinity, duration: 3.4 }}
          className="absolute bottom-4 left-1/2 -translate-x-1/2 w-16 h-24 bg-white text-black rounded-lg shadow-xl border rotate-[-8deg] flex items-center justify-center text-2xl"
        >
          ♠️ 10
        </motion.div>
      </motion.div>

      {/* Texte émotionnel */}
      <motion.p
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 1.5, duration: 1 }}
        className="mt-12 max-w-2xl text-center text-green-100 text-lg leading-relaxed"
      >
        Arbre32 est un duel mental.
        Deux esprits, deux visions, une seule structure.
        Chaque choix modifie la forme de l’arbre, définissant votre destin.
        Ici, le hasard existe… mais la stratégie domine.
      </motion.p>

      {/* Baseline finale */}
      <motion.p
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 2.2, duration: 1 }}
        className="mt-6 text-sm text-green-300 italic"
      >
        Construis. Anticipe. Surprends. Déploie ton intelligence.
      </motion.p>

    </div>
  );
}
