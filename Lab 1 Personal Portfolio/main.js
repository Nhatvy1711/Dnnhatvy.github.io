const menuBtn = document.getElementById("menu-btn");
const navLinks = document.getElementById("nav-links");
const menuBtnIcon = menuBtn.querySelector("i");

menuBtn.addEventListener("click", (e) => {
    navLinks.classList.toggle("open");
    const isOpen = navLinks.classList.contains("open");
    menuBtnIcon.setAttribute("class", isOpen ? "ri-close-line" : "ri-menu-line");
});

navLinks.addEventListener("click", (e) => {
    if (e.target.tagName === 'A') {
        navLinks.classList.remove("open");
        menuBtnIcon.setAttribute("class", "ri-menu-line");
    }
});

// Close menu when clicking outside
document.addEventListener('click', (e) => {
    const isClickInsideNav = navLinks.contains(e.target) || menuBtn.contains(e.target);
    if (!isClickInsideNav && navLinks.classList.contains('open')) {
        navLinks.classList.remove("open");
        menuBtnIcon.setAttribute("class", "ri-menu-line");
    }
});

// Scroll reveal animations
const scrollRevealOption = {
    distance: "50px",
    origin: "bottom",
    duration: 1000,
};

ScrollReveal().reveal(".header__image img", {
    ...scrollRevealOption,
    origin: "right",
});

ScrollReveal().reveal(".header__content h2", {
    ...scrollRevealOption,
    delay: 500,
});

ScrollReveal().reveal(".header__content h1", {
    ...scrollRevealOption,
    delay: 1000,
});

ScrollReveal().reveal(".header__content h3", {
    ...scrollRevealOption,
    delay: 1500,
});

ScrollReveal().reveal(".header__content p", {
    ...scrollRevealOption,
    delay: 2000,
});

ScrollReveal().reveal(".header__btns", {
    ...scrollRevealOption,
    delay: 2500,
});

ScrollReveal().reveal(".intro__image img", {
    ...scrollRevealOption,
    origin: "left",
});

ScrollReveal().reveal(".intro__content .section__subheader", {
    ...scrollRevealOption,
    delay: 500,
});

ScrollReveal().reveal(".intro__content .section__header", {
    ...scrollRevealOption,
    delay: 1000,
});

ScrollReveal().reveal(".intro__description", {
    ...scrollRevealOption,
    delay: 1500,
});

ScrollReveal().reveal(".intro__grid", {
    ...scrollRevealOption,
    delay: 2000,
});

ScrollReveal().reveal(".intro__content h4", {
    ...scrollRevealOption,
    delay: 2500,
});

ScrollReveal().reveal(".intro__flex div", {
    ...scrollRevealOption,
    delay: 3000,
    interval: 500,
});

ScrollReveal().reveal(".journey__grid > div > div", {
    ...scrollRevealOption,
    interval: 500,
});

ScrollReveal().reveal(".service__card", {
    ...scrollRevealOption,
    interval: 300,
});

ScrollReveal().reveal(".blog__card", {
    ...scrollRevealOption,
    interval: 300,
});

document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo Mixitup
    const mixer = mixitup('.portfolio__grid', {
        selectors: {
            target: '.mix'
        },
        animation: {
            duration: 300,
            effects: 'fade scale(0.5)'
        }
    });

    // Thêm event listener cho portfolio buttons
    const portfolioButtons = document.querySelectorAll('.portfolio__nav .btn');
    portfolioButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class từ tất cả buttons
            portfolioButtons.forEach(btn => btn.classList.remove('mixitup-control-active'));
            // Add active class cho button được click
            this.classList.add('mixitup-control-active');
        });
    });
});

// Banner animations
ScrollReveal().reveal(".banner__content", {
    distance: "50px",
    origin: "left",
    duration: 1000,
    delay: 300,
});

ScrollReveal().reveal(".banner__image", {
    distance: "50px",
    origin: "right",
    duration: 1000,
    delay: 500,
});

// Contact Me button functionality
document.querySelector('.banner__btn .btn--white')?.addEventListener('click', function(e) {
    e.preventDefault();
    
    // Add click animation
    this.style.transform = 'scale(0.95)';
    setTimeout(() => {
        this.style.transform = '';
    }, 150);
    
    // Handle contact action
    const email = 'dnnvy17@gmail.com';
    const subject = 'Project Collaboration Inquiry';
    const body = 'Hello Nhat Vy,\n\nI would like to discuss a project with you.\n\nBest regards,';
    
    window.open(`mailto:${email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`, '_blank');
});

// Blog read more buttons
document.querySelectorAll('.btn--blog').forEach(button => {
    button.addEventListener('click', function(e) {
        e.preventDefault();
        const blogTitle = this.closest('.blog__card').querySelector('.blog__title').textContent;
        alert(`Reading more about: ${blogTitle}`);
    });
});

// Download CV button
document.querySelector('.btn--download')?.addEventListener('click', function(e) {
    e.preventDefault();
    alert('Downloading CV...');
    // Thêm logic download CV thực tế ở đây
});

// Hire Me button
document.querySelector('.btn--nav')?.addEventListener('click', function(e) {
    e.preventDefault();
    const contactSection = document.querySelector('#contact');
    if (contactSection) {
        contactSection.scrollIntoView({ 
            behavior: 'smooth',
            block: 'start'
        });
    }
});