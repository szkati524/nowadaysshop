import React from 'react';
import { FiMail, FiPhone, FiMapPin, FiClock, FiHelpCircle } from 'react-icons/fi';

export const ContactPage: React.FC = () => {
  return (
    <div className="container mx-auto px-4 py-10 max-w-4xl">
     
      <div className="text-center mb-12">
        <h1 className="text-3xl font-bold text-slate-900 mb-3">Skontaktuj się z nami</h1>
        <p className="text-slate-600 max-w-xl mx-auto">
          Jesteśmy tutaj, aby Ci pomóc. Wybierz dogodną dla siebie formę kontaktu lub odwiedź naszą siedzibę.
        </p>
      </div>

     
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12">
      
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex items-start gap-4 hover:shadow-md transition">
          <div className="p-3 bg-indigo-50 text-indigo-600 rounded-lg shrink-0">
            <FiMail className="w-6 h-6" />
          </div>
          <div>
            <h3 className="font-semibold text-slate-800 text-lg mb-1">Napisz do nas</h3>
            <p className="text-sm text-slate-500 mb-2">Odpowiadamy zazwyczaj w ciągu 24 godzin.</p>
            <a href="mailto:kontakt@nowadays.pl" className="text-indigo-600 font-semibold hover:underline">
              kontakt@nowadays.pl
            </a>
          </div>
        </div>

       
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex items-start gap-4 hover:shadow-md transition">
          <div className="p-3 bg-indigo-50 text-indigo-600 rounded-lg shrink-0">
            <FiPhone className="w-6 h-6" />
          </div>
          <div>
            <h3 className="font-semibold text-slate-800 text-lg mb-1">Zadzwoń do nas</h3>
            <p className="text-sm text-slate-500 mb-2">Infolinia techniczna i sprzedażowa.</p>
            <a href="tel:+48123456789" className="text-indigo-600 font-semibold hover:underline">
              +48 123 456 789
            </a>
          </div>
        </div>

        
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex items-start gap-4 hover:shadow-md transition">
          <div className="p-3 bg-indigo-50 text-indigo-600 rounded-lg shrink-0">
            <FiMapPin className="w-6 h-6" />
          </div>
          <div>
            <h3 className="font-semibold text-slate-800 text-lg mb-1">Adres biura</h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              NowadaysShop Sp. z o.o.<br />
              ul. Zmyslona 12/4<br />
              00-001 Warszawa
            </p>
          </div>
        </div>

      
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex items-start gap-4 hover:shadow-md transition">
          <div className="p-3 bg-indigo-50 text-indigo-600 rounded-lg shrink-0">
            <FiClock className="w-6 h-6" />
          </div>
          <div>
            <h3 className="font-semibold text-slate-800 text-lg mb-1">Godziny otwarcia</h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              Poniedziałek – Piątek: 8:00 – 16:00<br />
              Sobota – Niedziela: Zamknięte
            </p>
          </div>
        </div>
      </div>

      
      <div className="bg-slate-900 text-white rounded-2xl p-8 shadow-md">
        <div className="flex items-center gap-3 mb-6">
          <FiHelpCircle className="w-6 h-6 text-indigo-400" />
          <h2 className="text-xl font-bold">Często zadawane pytania</h2>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-sm">
          <div>
            <h4 className="font-semibold text-indigo-300 mb-1">Jak szybko realizujecie zamówienia?</h4>
            <p className="text-slate-300">
              Wszystkie zamówienia złożone do godziny 12:00 wysyłamy tego samego dnia roboczego.
            </p>
          </div>
          <div>
            <h4 className="font-semibold text-indigo-300 mb-1">Jakie są formy płatności?</h4>
            <p className="text-slate-300">
              Umożliwiamy płatność ze środków przypisanych do Twojego konta użytkownika w serwisie.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};