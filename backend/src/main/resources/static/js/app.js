document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-confirm]').forEach(form => {
        form.addEventListener('submit', event => {
            if (!window.confirm(form.dataset.confirm)) event.preventDefault();
        });
    });

    const money = value => Number(value || 0).toLocaleString('es-PE', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    initPointOfSale(money);
    initTableTransaction(money);
});

function initPointOfSale(money) {
    const form = document.querySelector('[data-pos-form]');
    if (!form) return;

    const cards = [...form.querySelectorAll('.pos-product-card')];
    const cartContainer = form.querySelector('[data-cart-items]');
    const fieldsContainer = form.querySelector('[data-cart-fields]');
    const emptyState = form.querySelector('[data-cart-empty]');
    const cart = new Map();
    let category = 'all';

    const productFromCard = card => ({
        id: card.dataset.id,
        name: card.dataset.name,
        code: card.dataset.code,
        price: Number(card.dataset.price),
        stock: Number(card.dataset.stock),
        image: card.querySelector('img').src
    });

    function add(product, amount = 1) {
        const current = cart.get(product.id);
        const quantity = Math.min((current?.quantity || 0) + amount, product.stock);
        if (quantity < 1) return;
        cart.set(product.id, {...product, quantity});
        renderCart();
    }

    function changeQuantity(id, difference) {
        const item = cart.get(id);
        if (!item) return;
        const quantity = item.quantity + difference;
        if (quantity <= 0) cart.delete(id);
        else item.quantity = Math.min(quantity, item.stock);
        renderCart();
    }

    function renderCart() {
        cartContainer.querySelectorAll('.cart-item').forEach(element => element.remove());
        fieldsContainer.replaceChildren();
        emptyState.classList.toggle('d-none', cart.size > 0);
        let total = 0;

        [...cart.values()].forEach((item, index) => {
            total += item.price * item.quantity;
            const row = document.createElement('div');
            row.className = 'cart-item';

            const image = document.createElement('img');
            image.src = item.image;
            image.alt = '';

            const detail = document.createElement('div');
            const name = document.createElement('div');
            name.className = 'cart-item-name';
            name.textContent = item.name;
            const price = document.createElement('div');
            price.className = 'cart-item-price';
            price.textContent = `S/ ${money(item.price)} c/u`;
            const controls = document.createElement('div');
            controls.className = 'quantity-control';
            const minus = button('−', () => changeQuantity(item.id, -1));
            const quantity = document.createElement('span');
            quantity.textContent = item.quantity;
            const plus = button('+', () => changeQuantity(item.id, 1));
            plus.disabled = item.quantity >= item.stock;
            controls.append(minus, quantity, plus);
            detail.append(name, price, controls);

            const amount = document.createElement('div');
            amount.className = 'cart-item-total';
            const amountText = document.createElement('strong');
            amountText.textContent = `S/ ${money(item.price * item.quantity)}`;
            const remove = document.createElement('button');
            remove.type = 'button';
            remove.className = 'remove-cart-item';
            remove.innerHTML = '<i class="bi bi-trash3"></i> Quitar';
            remove.addEventListener('click', () => { cart.delete(item.id); renderCart(); });
            amount.append(amountText, remove);
            row.append(image, detail, amount);
            cartContainer.append(row);

            fieldsContainer.append(hidden(`items[${index}].productId`, item.id));
            fieldsContainer.append(hidden(`items[${index}].quantity`, item.quantity));
        });

        form.querySelector('[data-cart-count]').textContent = [...cart.values()].reduce((sum, item) => sum + item.quantity, 0);
        form.querySelector('[data-cart-subtotal]').textContent = `S/ ${money(total)}`;
        form.querySelector('[data-cart-total]').textContent = `S/ ${money(total)}`;
        form.querySelector('[data-checkout]').disabled = cart.size === 0;
    }

    function button(label, action) {
        const element = document.createElement('button');
        element.type = 'button';
        element.textContent = label;
        element.addEventListener('click', action);
        return element;
    }

    function hidden(name, value) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;
        input.value = value;
        return input;
    }

    cards.forEach(card => card.querySelector('[data-add-product]').addEventListener('click', () => add(productFromCard(card))));

    form.querySelectorAll('[data-initial-items] span').forEach(saved => {
        const card = cards.find(item => item.dataset.id === saved.dataset.productId);
        if (card) add(productFromCard(card), Number(saved.dataset.quantity || 1));
    });

    function filterProducts() {
        const term = form.querySelector('[data-pos-search]').value.trim().toLowerCase();
        let visible = 0;
        cards.forEach(card => {
            const matchesText = `${card.dataset.name} ${card.dataset.code}`.toLowerCase().includes(term);
            const matchesCategory = category === 'all' || card.dataset.category === category;
            const show = matchesText && matchesCategory;
            card.classList.toggle('d-none', !show);
            if (show) visible++;
        });
        form.querySelector('[data-no-products]').classList.toggle('d-none', visible !== 0);
    }

    form.querySelector('[data-pos-search]').addEventListener('input', filterProducts);
    form.querySelectorAll('[data-category-filter] button').forEach(button => {
        button.addEventListener('click', () => {
            form.querySelectorAll('[data-category-filter] button').forEach(item => item.classList.remove('active'));
            button.classList.add('active');
            category = button.dataset.category;
            filterProducts();
        });
    });

    renderCart();
}

function initTableTransaction(money) {
    const transaction = document.querySelector('[data-transaction-form]');
    if (!transaction) return;
    const body = transaction.querySelector('[data-items-body]');
    const template = transaction.querySelector('#item-row-template');
    const mode = transaction.dataset.transactionForm;

    function reindex() {
        [...body.querySelectorAll('tr')].forEach((row, index) => {
            row.querySelectorAll('[name]').forEach(input => {
                input.name = input.name.replace(/items\[\d+\]/, `items[${index}]`);
            });
        });
    }

    function updateTotal() {
        let total = 0;
        body.querySelectorAll('tr').forEach(row => {
            const option = row.querySelector('[data-product]').selectedOptions[0];
            const quantity = Number(row.querySelector('[data-quantity]').value || 0);
            const price = mode === 'sale' ? Number(option?.dataset.price || 0) : Number(row.querySelector('[data-price]').value || 0);
            total += quantity * price;
        });
        transaction.querySelector('[data-grand-total]').textContent = `S/ ${money(total)}`;
    }

    function updateRow(row) {
        const option = row.querySelector('[data-product]').selectedOptions[0];
        const quantity = Number(row.querySelector('[data-quantity]').value || 0);
        const priceInput = row.querySelector('[data-price]');
        const price = mode === 'sale' ? Number(option?.dataset.price || 0) : Number(priceInput.value || 0);
        if (mode === 'sale') {
            priceInput.value = money(price);
            row.querySelector('[data-stock]').textContent = option?.dataset.stock || '0';
        }
        row.querySelector('[data-subtotal]').textContent = `S/ ${money(price * quantity)}`;
        updateTotal();
    }

    function bindRow(row) {
        row.querySelectorAll('select,input').forEach(input => input.addEventListener('input', () => updateRow(row)));
        row.querySelector('[data-remove]').addEventListener('click', () => {
            if (body.children.length === 1) {
                row.querySelectorAll('select,input').forEach(input => input.value = '');
                row.querySelector('[data-quantity]').value = 1;
                updateRow(row);
            } else {
                row.remove(); reindex(); updateTotal();
            }
        });
        updateRow(row);
    }

    body.querySelectorAll('tr').forEach(bindRow);
    transaction.querySelector('[data-add-item]').addEventListener('click', () => {
        const html = template.innerHTML.replaceAll('__INDEX__', body.children.length);
        body.insertAdjacentHTML('beforeend', html);
        bindRow(body.lastElementChild);
    });
}
